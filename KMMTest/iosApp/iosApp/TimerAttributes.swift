//
//  TimerAttributes.swift
//  iosApp
//
//  Created by Greg Zenkov on 10/21/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import ActivityKit

public struct TimerAttributes: ActivityAttributes {
    
    public typealias TimerState = ContentState
    
    public struct ContentState: Codable, Hashable {
        var timeLeft: Int
    }
    
    var actionName: String
}
