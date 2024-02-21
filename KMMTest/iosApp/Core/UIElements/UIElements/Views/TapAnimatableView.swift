//
//  TapAnimatableView.swift
//  UIElements
//
//  Created by Greg Zenkov on 2/18/24.
//

import SwiftUI
import Combine

public struct TapAnimatableView: View {
    
    public let systemName: String
    public let isSelected: Bool
    
    public init(systemName: String, isSelected: Bool) {
        self.systemName = systemName
        self.isSelected = isSelected
    }
    
    @StateObject private var viewModel = TapAnimatableViewModel()
    
    private var degrees: Angle {
        if viewModel.isAnimating || viewModel.isTicked {
            return .degrees(viewModel.isTicked ? 60 : -60)
        } else {
            return .degrees(0)
        }
    }
    
    public var body: some View {
        VStack {
            Image(systemName: systemName)
                .resizable()
                .scaledToFit()
                .frame(width: 24, height: 24)
                .foregroundStyle(isSelected ? .blue : .red)
                .border(Color.black)
                //.animation(.easeInOut, value: viewModel.isAnimating)
                .rotationEffect(
                    degrees
                )
                .scaleEffect(viewModel.isAnimating ? 1.3 : 1.1)
            
            Text(isSelected ?  "Tab" : "AAAAA" )
                .foregroundStyle(isSelected ? .black : .gray)
        }
        .onChange(of: isSelected, perform: { value in
            guard value else {
                viewModel.stopAnimating()
                return
            }
            viewModel.startAnimating()
        })
    }
}

extension TapAnimatableView {
    final private class TapAnimatableViewModel: ObservableObject {
        @Published var isAnimating = false
        @Published var isTicked = false
        
        private var tickTimer: Timer?
        private var animationTimer: Timer?
        
//        private var set = Set<AnyCancellable>()
//        
//        init() {
//            Publishers.CombineLatest($isAnimating, $isTicked)
//                .receive(on: DispatchQueue.main)
//                .map({ value -> Bool in
//                    value.0 && value.1
//                })
//                .sink { [weak self] value in
//                    withAnimation { self?.canAnimate = value }
//                }
//                .store(in: &set)
//        }
        
        func startAnimating() {
            guard !isAnimating else { return }
            
            withAnimation(.default) {
                isAnimating = true
            }
            
            tickTimer = Timer.scheduledTimer(withTimeInterval: 0.1, repeats: true, block: { [weak self] _ in
                withAnimation(.easeInOut) {
                    self?.isTicked.toggle()
                }
            })
            //tickTimer?.fire()
            
            animationTimer = Timer.scheduledTimer(withTimeInterval: 0.5, repeats: false, block: { [weak self] _ in
                self?.stopAnimating()
            })
//            animationTimer?.fire()
        }
        
        func stopAnimating() {
            withAnimation(.easeInOut) {
                isAnimating = false
                isTicked = false
            }
            tickTimer?.invalidate()
            animationTimer?.invalidate()
            tickTimer = nil
            animationTimer = nil
        }
    }
}

#Preview {
    TapAnimatableView(systemName: "gearshape.2.fill", isSelected: true)
}
