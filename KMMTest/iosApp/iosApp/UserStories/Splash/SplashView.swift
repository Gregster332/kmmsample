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
import UIElements

struct SplashNavigateView: View {
    let child: SplashChild
    let navigateAuth: () -> Void
    let onBackPressed: () -> Void
    
    @Environment(\.colorScheme) var colorScheme
    
    var body: some View {
        switch child {
        case let main as SplashChild.Main:
            ZStack {
                
                Color(
                    uiColor: MR.colors.shared.backgroundColor.getUIColor()
                )
                .ignoresSafeArea(.all)
                
//                VStack {
//                    Text("Main")
//                    
//                    Button {
//                        navigateAuth()
//                    } label: {
//                        Text("push")
//                    }
//                }
                //.preferredColorScheme(appTheme.scheme)
                .onAppear {
                    print("on appear Main")
                }
                .onDisappear {
                    print("on disappear main")
                }
            }
        case let chats as SplashChild.ChatsMain:
            MainPageView(chats.component)
            //.preferredColorScheme(scheme)
            .onAppear {
                print("on appear chats")
            }
            .onDisappear {
                print("on disappear chats")
            }
        case let auth as SplashChild.Auth:
            AuthView(component: auth.component)
                //.preferredColorScheme(scheme)
        default:
            EmptyView()
        }
    }
}

struct SplashContent: View {
    
    @ObservedObject
    private var childStack: ObservableValue<ChildStack<AnyObject, SplashChild>>
    
    @ObservedObject
    private var sheet: ObservableValue<ChildSlot<AnyObject, SplashSheetChild>>
    
    @Environment(\.colorScheme) var colorScheme
    
    private let component: Splash
    
    init(
        _ component: SplashComponent
    ) {
        self.childStack = ObservableValue(component.childStack)
        self.sheet = ObservableValue(component.sheet)
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
                .environment(\.colorScheme, colorScheme)
            }
        )
        .sheet(
            item: sheet.value.child?.instance,
            content: { child in
                switch child {
                case let child as SplashSheetChild.AppTheme:
                    AppThemeView(child.component)
                        .environment(\.colorScheme, colorScheme)
                        .presentationDetents([.fraction(0.3)])
                default:
                    EmptyView()
                }
            })
    }
}

extension View {
    func sheet<T, Content>(
        item: T?,
        onDismiss: (() -> Void)? = nil,
        @ViewBuilder content: @escaping (T) -> Content
    ) -> some View where Content: View {
        sheet(
            isPresented: .init(
                get: { item != nil },
                set: { _ in }
            ),
            onDismiss: { onDismiss?() },
            content: { content(item!) }
        )
    }
}

struct SplashView: View {
    @State
    private var componentHandler = ComponentsHandler {
        SplashComponent(componentContext: $0)
    }
    @AppStorage("app_theme")
    private var appTheme: AppThemSwitcherView.AppTheme = .systemDefault
    
    var body: some View {
        SplashContent(componentHandler.component)
            .preferredColorScheme(appTheme.scheme)
            .onAppear {
                LifecycleRegistryExtKt.resume(self.componentHandler.lifecycle)
            }
            .onDisappear {
                LifecycleRegistryExtKt.stop(self.componentHandler.lifecycle)
            }
            .navigationBarHidden(true)
    }
}

private extension SplashView {
//    final class SplashViewWrapper: ObservableObject {
//        
//        private let viewModel: SplashViewModel
//        private var cancellables = Set<AnyCancellable>()
//        let coordinator = Coordinator.shared
//        
//        @Published var isLoading: Bool = false
//        @Published var authState: AuthorizeStateAdapter = .notSet
//        
//        init(viewModel: SplashViewModel = IosMainDI().splashViewModel()) {
//            self.viewModel = viewModel
//            bind()
//        }
//        
//        func onDisappear() {
//            cancellables.forEach { $0.cancel() }
//            cancellables = []
//        }
//        
//        private func bind() {
//            FlowPublisher<SplashStoreUISplashState>(flow: viewModel.state)
//                .receive(on: DispatchQueue.main)
//                .sink(receiveValue: { [weak self] state in
//                    guard let self = self else { return }
//                    self.isLoading = state.isLoading
//                    switch AuthorizeStateAdapter(state.authState) {
//                    case .autheticated:
//                        coordinator.showMain()
//                    case .reauth:
//                        coordinator.showMain()
//                       // coordinator.showAuth()
//                    default: break
//                    }
//                })
//                .store(in: &cancellables)
//        }
//    }
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

//curl 'https://securetoken.googleapis.com/v1/token?key=AIzaSyAxUZWecxbDqxZjdGoXaOITqllrlkmUbRU' \
//-H 'Content-Type: application/x-www-form-urlencoded' \
//--data 'grant_type=refresh_token&refresh_token=AMf-vByeyvCtPjdGw0-AoRWtS8dhIf3N06u8P3u7BuXwUUTVZE0YO-AgqIExvrQHhkDlv7yCKmsYfJ1v83mudubUY4eZ_0qQsgmbbNUH_m8F4AWh1FbVHFPTCoijb4ISlGU5Mc_lt01UjwKMKB7t4dmfhQOYLdvD7QM7ikFcuGloCT3gpkIL-CnhXtbN9vZw-3-p0yCZxM2YiOEr2DUkpqRzPp2q-Dc6Ne-IBi2AWasjwoE5kbgPksZNE7KRt1jWf4Ir7iEotE5E2-_S0MGAiiRMFUOeMLMQp8nE80Fng_mHcSBfso3v6GXRFlbmBDf_7rcMB9HLDhHl'
