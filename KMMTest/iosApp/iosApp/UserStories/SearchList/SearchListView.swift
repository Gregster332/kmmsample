//
//  SearchListView.swift
//  iosApp
//
//  Created by Greg Zenkov on 1/21/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import SharedModule

struct SearchListView: View {
    
    private let component: SearchList
    
    @ObservedObject
    private var models: ObservableValue<SearchListStoreSearchListUIState>
    
    init(component: SearchList) {
        self.component = component
        self.models = ObservableValue(component.value)
    }
    
    var body: some View {
        ScrollView(.vertical) {
            ForEach(models.value.users, id: \.self) { user in
                SearchListCell(name: user.nickname)
                    .contentShape(Rectangle())
                    .onTapGesture {
                        component.cacheResult(user: user)
                        //component.onTap()
                    }
            }
        }
    }
}

struct MainPageView: View {
    let component: MainPages
    
    @ObservedObject
    private var models: ObservableValue<MainPagesChildren>
    
    @FocusState private var focus: Bool
    @State private var text: String = ""
    
    init(_ component: MainPages) {
        self.component = component
        self.models = ObservableValue(component.children)
    }
    
    var body: some View {
        VStack(spacing: 0) {
            Rectangle()
                .fill(Color.clear)
                .frame(height: 40)
                .overlay {
                    HStack {
                        Image(systemName: "magnifyingglass")
                        
                        TextField("", text: $text)
                            .focused($focus)
                            
                    }
                    .padding(3)
                    .background {
                        RoundedRectangle(cornerRadius: 4)
                            .fill(Color.gray.opacity(0.1))
                    }
                    .padding(.horizontal, 3)
                    .onTapGesture {
                        component.list(open: true)
                    }
                }
            
            ZStack {
                Group {
                    if let search = models.value.searchListChild?.instance {
                        SearchListView(component: search)
                            .onAppear {
                                focus = true
                            }
                            .onChange(of: text) { newValue in
                                if !newValue.isEmpty {
                                    (search as SearchList).type(text: newValue)
                                }
                            }
                    } else {
                        ChatsView(
                            component: models.value.mainChild.instance
                        )
                    }
                }
                .transition(AnyTransition.opacity.animation(.smooth))
            }
            
        }
        .viewBackground(
            color: MR.colors.shared.backgroundColor.getUIColor()
        )
       
        .toolbar(content: {
            ToolbarItem(placement: .principal) {
                Text(models.value.searchListChild?.instance != nil ? "Search" : "Chats")
                    .font(.system(size: 18, weight: .medium))
                    .foregroundStyle(.primary)
            }
            
            ToolbarItem(placement: .topBarTrailing) {
                if models.value.searchListChild?.instance != nil {
                    Button {
                        focus = false
                        text = ""
                        component.list(open: false)
                    } label: {
                        Image(systemName: "xmark")
                            .foregroundStyle(.white)
                    }
                } else {
                    EmptyView()
                }
            }
        })
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarBackButtonHidden()
    }
}

struct SearchListCell: View {
    
    let name: String
    
    private let randomColors = [Color.blue, Color.red, .yellow, .green, .orange]
    
    var body: some View {
        VStack {
            HStack {
                nicknameCircle()
                Text(name)
            }
            .frame(maxWidth: .infinity, maxHeight: 40, alignment: .leading)
            .padding(.horizontal, 8)
            
            Divider()
        }
    }
    
    @ViewBuilder
    private func nicknameCircle() -> some View {
        ZStack {
            Circle()
                .fill(randomColors.randomElement()!.opacity(0.3))
                
            Text(name.first?.uppercased() ?? "")
        }
        .frame(height: 37)
    }
}

//#Preview {
//    SearchListView()
//}
