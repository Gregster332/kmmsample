//
//  ObservableValue.swift
//  iosApp
//
//  Created by Greg Zenkov on 1/13/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SharedModule
import SwiftUI

public class ObservableValue<T : AnyObject> : ObservableObject {
    @Published
    var value: T

    private var cancellation: Cancellation?
    
    init(_ value: SharedModule.Value<T>) {
        self.value = value.value
        self.cancellation = value.subscribe(observer_: { [weak self] value in
            //print("CVADADADDAADADA")
            self?.value = value
        })
    }

    deinit {
        cancellation?.cancel()
    }
}
