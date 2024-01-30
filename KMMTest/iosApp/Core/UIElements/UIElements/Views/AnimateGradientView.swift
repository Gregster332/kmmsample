//
//  AnimateGradientView.swift
//  UIElements
//
//  Created by Greg Zenkov on 12/27/23.
//

import SwiftUI

public struct AnimateGradientView: View, Equatable {
    public static func == (lhs: AnimateGradientView, rhs: AnimateGradientView) -> Bool {
        lhs.points == rhs.points
    }
    
    @Environment(\.colorScheme) var colorScheme
    @State private var proxy: GeometryProxy?
    @State private var points: [CGPoint] = []
    @State private var timer = Timer.publish(
        every: 5,
        on: RunLoop.main,
        in: .default)
        .autoconnect()
        .receive(on: RunLoop.main)
    
    public init() {}
    
    public var body: some View {
        GeometryReader { proxy in
            ZStack {
                ForEach(points.indices, id: \.self) { i in
                    Circle()
                        .fill(.green.opacity(colorScheme == .dark ? 0.2 : 0.7))
                        .blur(radius: 2)
                        .opacity(0.5)
                        .frame(
                            width: CGFloat.random(in: 90...520),
                            height: CGFloat.random(in: 90...520)
                        )
                        .offset(
                            x: points[i].x,
                            y: points[i].y
                        )
                }
            }
            .onAppear {
                self.proxy = proxy
                self.points = Array(repeating: .zero, count: 10)
                let width = proxy.frame(in: .global).width
                let height = proxy.frame(in: .global).height
                withAnimation(.easeInOut(duration: 4).repeatCount(1)) {
                    self.points = Array(repeating: .zero, count: 10)
                    self.points = points.map { _ in mapPoint(width, height) }
                }
            }
        }
        .ignoresSafeArea()
        .background(colorScheme == .dark ? .black : .white)
        .onReceive(timer) { _ in
            withAnimation(.easeOut(duration: 10)) {
                let width = (proxy?.frame(in: .global).width ?? 0)
                let height = (proxy?.frame(in: .global).height  ?? 0)
                self.points = points.map { _ in mapPoint(width, height) }
            }
        }
    }
    
    private func mapPoint(
        _ width: CGFloat,
        _ height: CGFloat
    ) -> CGPoint {
        return CGPoint(
            x: CGFloat.random(in: -(width/2)..<width),
            y: CGFloat.random(in: -(height/2)..<height)
        )
    }
}

#Preview {
    AnimateGradientView() as! any View
}
