//
//  ViewBackgroud.swift
//  iosApp
//
//  Created by Greg Zenkov on 2/17/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct ViewBackgroud: ViewModifier {
    let color: UIColor
    
    func body(content: Content) -> some View {
        ZStack {
            Color(uiColor: color)
                .ignoresSafeArea()
            
            content
        }
    }
}
