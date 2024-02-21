//
//  SplashView.swift
//  iosApp
//
//  Created by Greg Zenkov on 1/5/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Combine
import SharedModule
import SwiftUI
import UIElements

struct SplashContent: View {
    
    @ObservedObject
    private var child: ObservableValue<SplashSplashChild>
    
    @ObservedObject
    private var sheet: ObservableValue<ChildSlot<AnyObject, SplashSheetChild>>
    
    private let component: Splash
    
    init(
        _ component: SplashComponent
    ) {
        self.child = ObservableValue(component.child)
        self.sheet = ObservableValue(component.sheet)
        self.component = component
    }
    
    var body: some View {
        VStack {
            Group {
                if let tab = child.value.tabChild?.instance {
                    MainTabView(component: tab)
                }
                
                if let signUp = child.value.authChild?.instance {
                    AuthView(component: signUp)
                }
            }
            .transition(AnyTransition.opacity.animation(.easeInOut))
        }
        .viewBackground(
            color: MR.colors.shared.backgroundColor.getUIColor()
        )
        .sheet(
            item: sheet.value.child?.instance,
            isFullScreen: true,
            content: { child in
                switch child {
                case let debug as SplashSheetChild.DebugMenu:
                    DebugMenuView(component: debug.component)
                default:
                    EmptyView()
                }
                
            }
        )
//        .didShake {
//            component.didShakeDevice()
//        }
    }
}

struct SplashView: View {
    @State
    private var componentHandler = ComponentsHandler {
        SplashComponent(componentContext: $0)
    }
    
    @State
    private var appThemeController: ComponentsHandler<AppThemeController>
    
    @ObservedObject
    private var appTheme: ObservableValue<SharedModule.AppThemeEnum>
    
    init() {
        let appTheme = ComponentsHandler(factory: {
            AppThemeController(componentContext: $0)
        })
        self.appThemeController = appTheme
        self.appTheme = ObservableValue(appTheme.component.appTheme)
    }
    
    var body: some View {
        SplashContent(componentHandler.component)
            .preferredColorScheme(appTheme.value.colorScheme())
            .onAppear {
                LifecycleRegistryExtKt.resume(self.componentHandler.lifecycle)
                LifecycleRegistryExtKt.resume(self.appThemeController.lifecycle)
            }
            .onDisappear {
                LifecycleRegistryExtKt.stop(self.componentHandler.lifecycle)
                LifecycleRegistryExtKt.stop(self.appThemeController.lifecycle)
            }
            .navigationBarHidden(true)
            
    }
}

#Preview {
    SplashView()
}
