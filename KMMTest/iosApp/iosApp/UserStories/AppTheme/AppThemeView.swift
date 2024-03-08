//
//  AppThemeView.swift
//  iosApp
//
//  Created by Greg Zenkov on 1/13/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import SharedModule

struct OffsetPK: PreferenceKey {
    static var defaultValue: CGFloat = 0
    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value += nextValue()
    }
}

struct AppThemeView: View {
    
    @State private var offset: CGFloat = 0
    
    private let component: SettingsPage
    
    @ObservedObject
    private var settings: ObservableValue<SettingsStoreSettingsUIState>
    
    init(_ component: SettingsPage) {
        self.component = component
        self.settings = ObservableValue(component.settings)
    }
    
    var body: some View {
        ZStack {
            List {
                //Section {
                profileView()
                    .listRowBackground(Color.clear)
                    .listRowSeparator(.hidden)
                //}
                    .background {
                        GeometryReader { proxy in
                            Color.clear
                                .preference(
                                    key: OffsetPK.self,
                                    value: proxy.frame(in: .named("SCROLL")).origin.y
                                )
                        }
                    }
                    .onPreferenceChange(OffsetPK.self, perform: { value in
                        self.offset = value
                    })
                
                ForEach(0..<settings.value.sections.count, id: \.self) { index in
                    switch SettingsSectionsSEnum(settings.value.sections[index]) {
                    case .appearence(let theme):
                        appearenceSection(selectedTheme: theme)
                            .listRowBackground(Color.clear)
                            .listRowSeparator(.hidden)
                    case .def(let cells):
                        Section {
                            ForEach(cells, id: \.self) { cell in
                                HStack {
                                    Text(cell.title)
                                    if let info = cell.info {
                                        Text(info)
                                    }
                                }
                            }
                            .listRowSeparator(.hidden)
                            .listRowBackground(Color.clear)
                        }
                    }
                }
                
                logOutButton()
            }
            .listStyle(.inset)
            .coordinateSpace(name: "SCROLL")
        }
        .scrollContentBackground(.hidden)
        .viewBackground(
            color: MR.colors().backgroundColor.getUIColor()
        )
    }
    
    
    private func profileView() -> some View {
        var ideal = offset / 120
        if offset > 0 {
            ideal = 1
        } else {
            ideal = (-ideal) < 0 ? 0 : ideal
            ideal = ideal > 1 ? 1 : ideal
            ideal = 1 + ideal
        }
        
        return VStack(spacing: 8) {
            Circle()
                .fill(Color.blue)
                .frame(width: 100, height: 100)
            
            VStack(spacing: 4) {
                Text("\(offset)")
                    .font(.title)
                    .fontWeight(.semibold)
                
                HStack(spacing: 2) {
                    Text(settings.value.userEmail)
                        .tint(.primary)
                    Text("@\(settings.value.userName)")
                        .foregroundStyle(.gray)
                }
            }
        }
        .frame(maxWidth: .infinity)
        .scaleEffect(x: ideal, y: ideal)
        .opacity(ideal)
    }
    
    @ViewBuilder
    private func appearenceSection(
        selectedTheme: AppThemeEnum
    ) -> some View {
        Section {
            Picker(
                "",
                selection: Binding(
                    get: { selectedTheme },
                    set: { component.changeAppTheme(to: $0) })
            ) {
                ForEach(SharedModule.AppThemeEnum.entries, id: \.self) { theme in
                    Text(theme.name)
                }
            }
            .pickerStyle(.segmented)
        } header: {
            Text("App theme")
                .font(.system(size: 16, weight: .medium))
                .foregroundStyle(MR.colors().textPrimary.toSUIColor)
        }
    }
    
    @ViewBuilder
    private func logOutButton() -> some View {
        Text("Log Out")
            .font(.system(size: 18, weight: .semibold))
            .foregroundStyle(.red)
            .padding(8)
            .padding(.leading, 12)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background {
                RoundedRectangle(cornerRadius: 12)
                    .fill(
                        MR.colors().textFieldBGColor.toSUIColor.opacity(0.1))
            }
            .listRowBackground(Color.clear)
            .listRowSeparator(.hidden)
            .onTapGesture {
                component.logOut()
            }
    }
}

extension SharedModule.AppThemeEnum {
    func colorScheme() -> ColorScheme? {
        switch self {
        case .dark: return .dark
        case .light: return .light
        default:
            return nil
        }
    }
}

#Preview {
    AppThemeView(PreviewSettingsPageComponent())
}

enum SettingsSectionsSEnum {
    case appearence(SharedModule.AppThemeEnum)
    case def([SettingsStoreSettingsCell])
    
    init(_ obj: SettingsStoreSettingsSection) {
        if let obj = obj as? SettingsStoreSettingsSectionAppearence {
            self = .appearence(obj.selectedTheme)
        } else if let obj = obj as? SettingsStoreSettingsSectionDefault {
            self = .def(obj.cells)
        } else {
            fatalError()
        }
    }
}
