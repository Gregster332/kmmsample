//
//  AppThemeView.swift
//  iosApp
//
//  Created by Greg Zenkov on 1/13/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import SharedModule
import UIElements

struct AppThemeView: View {
    
    @Environment(\.colorScheme) var colorScheme
    private let component: AppThemeComponent
    
    init(_ component: AppThemeComponent) {
        self.component = component
    }
    
    var body: some View {
        ZStack {
            Color(MR.colors().backgroundColor.getUIColor())
                .ignoresSafeArea()
            
            AppThemSwitcherView()
                .environment(\.colorScheme, colorScheme)
        }
//        .onDisappear {
//            print("AppTheme on dispaper")
//            component.dismiss()
//        }
    }
}

//#Preview {
//    AppThemeView()
//}
