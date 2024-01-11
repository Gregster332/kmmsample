//
//  AppTheme+Extension.swift
//  iosApp
//
//  Created by Greg Zenkov on 1/6/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import UIElements
import SwiftUI

extension AppTheme {
    var scheme: ColorScheme? {
        switch self {
        case .systemDefault:
            return nil
        case .dark:
            return .dark
        case .light:
            return .light
        }
    }
}
