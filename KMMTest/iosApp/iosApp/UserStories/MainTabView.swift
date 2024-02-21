//
//  MainTabView.swift
//  iosApp
//
//  Created by Greg Zenkov on 2/16/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import SharedModule
import UIElements

struct MainTabView: View {
    
    let component: TabComponent
    
    @ObservedObject
    private var models: ObservableValue<TabTabChild>
    
    init(component: TabComponent) {
        self.component = component
        self.models = ObservableValue(component.children)
    }
    
    var body: some View {
        TabView {
            Group {
                MainPageView(models.value.mainPage.instance)
                    .tabItem { Label("Chats", systemImage: "message.fill") }
                
                AppThemeView(models.value.settings.instance)
                    .tabItem { Label("Settings", systemImage: "gearshape.2.fill") }
            }
        }
        .tint(.primary)
        .navigationBarBackButtonHidden()
        .onAppear {
            let appearance = UITabBarAppearance()
            appearance.backgroundEffect = UIBlurEffect(style: .systemUltraThinMaterial)
            appearance.backgroundColor = MR.colors().backgroundColor.getUIColor()
            UITabBar.appearance().standardAppearance = appearance
            UITabBar.appearance().scrollEdgeAppearance = appearance
        }
    }
}
