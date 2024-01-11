//
//  SplashView.swift
//  iosApp
//
//  Created by Greg Zenkov on 1/5/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Combine
import SwiftUI
import SharedModule

public class ObservableValue<T : AnyObject> : ObservableObject {
    @Published
    var value: T

    private var cancellation: Cancellation?
    
    init(_ value: SharedModule.Value<T>) {
        self.value = value.value
        self.cancellation = value.observe(observer: { [weak self] value in
            self?.value = value
        })
    }

    deinit {
        cancellation?.cancel()
    }
}

struct SplashNavigateView: View {
    let child: SplashChild
    let navigateAuth: () -> Void
    let onBackPressed: () -> Void
    
    var body: some View {
        switch child {
        case let main as SplashChild.Main:
            VStack {
                Text("Main")
                
                Button {
                    navigateAuth()
                } label: {
                    Text("push")
                }

            }
            .onAppear {
                print("on appear Main")
            }
            .onDisappear {
                print("on disappear main")
            }
        case let auth as SplashChild.Auth:
            VStack {
                Text("Auth")
            }
//            .onDisappear(perform: {
//                onBackPressed()
//                
//            })
            .onAppear {
                print("on appear auth")
            }
            .onDisappear {
                print("on disappear auth")
            }
        default:
            EmptyView()
        }
    }
}

struct SplashContent: View {
    
    @ObservedObject
    private var childStack: ObservableValue<ChildStack<AnyObject, SplashChild>>
    private let component: Splash
    
    init(
        _ component: SplashComponent
    ) {
        self.childStack = ObservableValue(component.childStack)
        self.component = component
    }
    
    var body: some View {
        StackView(
            stackValue: childStack,
            getTitle: { _ in "" },
            onBack: component.onBackPressed,
            childContent: { child in
                SplashNavigateView.init(
                    child: child,
                    navigateAuth: component.navigateAuth,
                    onBackPressed: component.onBackPressed
                )
            }
        )
    }
}

struct SplashView: View {
    
    //@StateObject private var wrapper = SplashViewWrapper()
    @State
    private var componentHandler = ComponentsHandler {
        SplashComponent(componentContext: $0)
    }
    
    var body: some View {
        SplashContent(componentHandler.component)
            .onAppear { LifecycleRegistryExtKt.resume(self.componentHandler.lifecycle)
            }
            .onDisappear {
                LifecycleRegistryExtKt.stop(self.componentHandler.lifecycle)
                //wrapper.onDisappear()
            }
            .navigationBarHidden(true)
    }
}

private extension SplashView {
    final class SplashViewWrapper: ObservableObject {
        
        private let viewModel: SplashViewModel
        private var cancellables = Set<AnyCancellable>()
        let coordinator = Coordinator.shared
        
        @Published var isLoading: Bool = false
        @Published var authState: AuthorizeStateAdapter = .notSet
        
        init(viewModel: SplashViewModel = IosMainDI().splashViewModel()) {
            self.viewModel = viewModel
            bind()
        }
        
        func onDisappear() {
            cancellables.forEach { $0.cancel() }
            cancellables = []
        }
        
        private func bind() {
            FlowPublisher<SplashStoreUISplashState>(flow: viewModel.state)
                .receive(on: DispatchQueue.main)
                .sink(receiveValue: { [weak self] state in
                    guard let self = self else { return }
                    self.isLoading = state.isLoading
                    switch AuthorizeStateAdapter(state.authState) {
                    case .autheticated:
                        coordinator.showMain()
                    case .reauth:
                        coordinator.showMain()
                       // coordinator.showAuth()
                    default: break
                    }
                })
                .store(in: &cancellables)
        }
    }
}

enum AuthorizeStateAdapter {
    case notSet
    case autheticated
    case reauth
    
    init(_ obj: SplashStoreAuthorizeState) {
        if obj is SplashStoreAuthorizeStateAutheticated {
            self = .autheticated
        } else if obj is SplashStoreAuthorizeStateReauth {
            self = .reauth
        } else {
            self = .notSet
        }
    }
}

#Preview {
    SplashView()
}
