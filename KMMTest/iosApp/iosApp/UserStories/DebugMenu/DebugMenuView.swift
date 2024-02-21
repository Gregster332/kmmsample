//
//  DebugMenuView.swift
//  iosApp
//
//  Created by Greg Zenkov on 2/10/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import SharedModule

struct DebugMenuView: View {
    
    let component: DebugMenu
    
    @ObservedObject
    private var models: ObservableValue<SettingsSections>
    
    init(component: DebugMenu) {
        self.component = component
        self.models = ObservableValue(component.settings)
    }
    
    var body: some View {
        NavigationStack {
            List {
                booleanSection(boolSection: models.value.boolSection)
                stringsSection(section: models.value.stringsSection)
            }
            .listStyle(.insetGrouped)
            .toolbar {
                ToolbarItem(placement: .principal) {
                    Text("DebugMenu")
                        .font(.system(size: 16, weight: .semibold))
                }
                ToolbarItem(placement: .topBarTrailing) {
                    Button(action: {
                        component.close()
                    }, label: {
                        Image(systemName: "xmark")
                            .foregroundStyle(.primary)
                    })
                }
            }
            .navigationBarTitleDisplayMode(.inline)
            .toolbarBackground(.visible, for: .navigationBar)
        }
    }
    
    @ViewBuilder
    private func booleanSection(boolSection: BooleanSection) -> some View {
        Section(boolSection.title) {
            ForEach(0..<boolSection.settings.count, id: \.self) { i in
                HStack {
                    Text("\(boolSection.settings[i].key)")
                        .lineLimit(1)
                        .minimumScaleFactor(0.1)
                    
                    Spacer()
                    
                    Toggle("", isOn: Binding(get: {
                        return boolSection.settings[i].value
                    }, set: {
                        component.updateSettings(value: boolSection.settings[i], newValue: $0)
                    }))
                }
            }
        }
    }
    
    @ViewBuilder
    private func stringsSection(section: StringsSection) -> some View {
        Section(section.title) {
            ForEach(0..<section.settings.count, id: \.self) { i in
                HStack {
                    Text("\(section.settings[i].key)")
                        .lineLimit(1)
                        .minimumScaleFactor(0.1)
                    
                    Spacer()
                    
                    Text("\(section.settings[i].value)")
                }
            }
        }
    }
}

//#Preview {
//    DebugMenuView(component: DebugMenuPreview())
//}
