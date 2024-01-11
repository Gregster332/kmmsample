//
//  ComponentsHandler.swift
//  iosApp
//
//  Created by Greg Zenkov on 1/9/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SharedModule

class ComponentsHandler<Component> {
    let lifecycle: LifecycleRegistry
    let component: Component
    
    init(factory: (ComponentContext) -> Component) {
        let lifecycle = LifecycleRegistryKt.LifecycleRegistry()
        let component = factory(DefaultComponentContext(lifecycle: lifecycle))
        self.lifecycle = lifecycle
        self.component = component
        lifecycle.onCreate()
    }
    
    deinit {
        lifecycle.onDestroy()
    }
}
