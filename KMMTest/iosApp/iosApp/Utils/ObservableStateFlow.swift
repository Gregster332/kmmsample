//
//  Publishers+Flow.swift
//  iosApp
//
//  Created by Greg Zenkov on 9/24/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Combine
import SharedModule

final class ObservableStateFlow<T: AnyObject>: ObservableObject {
    private let observableFlow: AnyStateFlow<T>
    @Published var value: T
    private var observer: SharedModule.Cancellable?
    
    init(_ flow: AnyStateFlow<T>) {
        observableFlow = flow
        self.value = flow.value
        observer = observableFlow.collect(
            onEach: { [weak self] value in self?.value = value }
        )
    }
    
    deinit {
        observer?.cancel()
    }
}
