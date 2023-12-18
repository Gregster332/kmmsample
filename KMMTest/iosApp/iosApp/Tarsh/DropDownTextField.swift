//
//  DropDownTextField.swift
//  iosApp
//
//  Created by Greg Zenkov on 10/9/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import Accelerate
import Combine

struct ViewSizeReader: ViewModifier {
    
    @Binding var viewSize: CGSize
    
    func body(content: Content) -> some View {
        content
            .background {
                GeometryReader(content: { geometry in
                    Rectangle()
                        .fill(.clear)
                        .onAppear {
                            viewSize = geometry.size
                        }
                })
            }
    }
}

extension View {
    func readSize(_ size: Binding<CGSize>) -> some View {
        modifier(ViewSizeReader(viewSize: size))
    }
}

struct NewView: View {
    
    @StateObject var recoreder = AudioRecorder()
    
    
    var body: some View {
        ZStack {
            
            LiqView(interval: 0.4, ballsCount: 22, voiceOffset: $recoreder.peek)
                .foregroundStyle(.blue.opacity(0.2))
                .frame(width: 120, height: 120)
            
            LiqView(interval: 0.8, ballsCount: 18, voiceOffset: .constant(0))
                .foregroundStyle(.blue.opacity(0.4))
                .frame(width: 90, height: 90)
            
            ZStack {
                Circle()
                    .fill(Color.blue.opacity(0.9))
                Image(systemName: "arrow.up")
                    .font(.system(size: 22, weight: .bold))
                    .foregroundStyle(.white)
                    .onTapGesture {
                        recoreder.startRecord()
                    }
            }
            .frame(width: 70, height: 70)
            .mask {
                LiqView(interval: 1.2, ballsCount: 16, voiceOffset: .constant(0))
            }
        }
    }
}

struct LiqView: View {
    
    @State private var size: CGSize = .zero
    @State private var positions: [CGPoint] = []
    @Binding var voiceOffset: Double
    
    let interval: TimeInterval
    let ballsCount: Int
    
    private let blurRadius = 11.0
    private let thres = 0.2
    
    @State private var cancellableTimer: AnyCancellable?
    
    init(interval: TimeInterval, ballsCount: Int, voiceOffset: Binding<Double>) {
        self.interval = interval
        self.ballsCount = ballsCount
        self._voiceOffset = voiceOffset
    }
    
    var body: some View {
        Circle()
            .readSize($size)
            .mask {
                Canvas { context, size in
                    let circles = (0..<ballsCount)
                        .compactMap { context.resolveSymbol(id: $0) }
                    
                    context.addFilter(.alphaThreshold(min: thres))
                    context.addFilter(.blur(radius: blurRadius))
                    context.drawLayer { context2 in
                        circles.forEach { circle in
                            context2.draw(circle, at: .init(x: size.width / 2, y: size.height / 2))
                        }
                    }
                } symbols: {
                    ForEach(positions.indices, id: \.self) { pos in
                        Circle()
                            .fill(.red)
                            .tag(pos)
                            .frame(width: pos == 0 ? size.width - (size.width / 4) : size.width/2)
                            .offset(
                                x: pos == 0 ? 0 : positions[pos].x,
                                y: pos == 0 ? 0 : positions[pos].y
                            )
                    }
                }
            }
            .onAppear {
                positions = Array(repeating: .zero, count: ballsCount)
                startTimer()
            }
            .onDisappear {
                stopTimer()
            }
            .onChange(of: voiceOffset) { newValue in
                let div = (100 - abs(newValue)) * 0.1
                print(div)
                guard div >= 7 else {
//                    withAnimation(.easeInOut(duration: 2)) {
//                        positions = positions.map { _ in
//                            randomPosition(in: size)
//                        }
//                    }
                    return
                }
                
                withAnimation(.easeInOut(duration: 0.5)) {
                    positions = positions.map { point in
                        switch (point.x >= 0, point.y >= 0) {
                        case (true, true):
                            return CGPoint(
                                x: point.x + div,
                                y: point.y + div
                            )
                        case (false, true):
                            return CGPoint(
                                x: point.x - div,
                                y: point.y + div
                            )
                        case (true, false):
                            return CGPoint(
                                x: point.x + div,
                                y: point.y - div
                            )
                        case (false, false):
                            return CGPoint(
                                x: point.x - div,
                                y: point.y - div
                            )
                        }
                    }
                    //print(positions[0])
                }
            }
    }
    
    private func randomPosition(in bounds: CGSize) -> CGPoint {
        let pointer: OpaquePointer = create_pointer(
            bounds.width,
            bounds.height
        )
        
        let x = pointer_x(pointer)
        let y = pointer_y(pointer)
        
        return CGPoint(x: x, y: y)
    }
    
    private func startTimer() {
        cancellableTimer = Timer.publish(
            every: interval,
            on: .main,
            in: .default
        )
        .autoconnect()
        .sink(receiveValue: { _ in
            withAnimation(.easeOut(duration: 2)) {
                positions = positions.map { _ in
                    randomPosition(in: size)
                }
            }
        })
    }
    
    private func stopTimer() {
        cancellableTimer?.cancel()
        cancellableTimer = nil
    }
}

#Preview {
    NewView()
}
