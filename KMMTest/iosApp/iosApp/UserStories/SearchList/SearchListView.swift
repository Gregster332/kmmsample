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
    
    let component: SearchList
    
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
            }
        }
    }
}

struct MainPageView: View {
    let component: MainPages
    
    @ObservedObject
    private var models: ObservableValue<MainPagesChildren>
    
    @FocusState private var focus: Bool
    
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
                        
                        TextField("", text: .constant("Hello"))
                            .focused($focus)
                        
                    }
                    .padding(3)
                    .background {
                        RoundedRectangle(cornerRadius: 4)
                            .fill(Color.gray.opacity(0.1))
                    }
                    .padding(.horizontal, 3)
                }
            
            ZStack {
                Group {
                    if let search = models.value.searchListChild?.instance {
                        SearchListView(component: search)
                    } else {
                        ChatsView(
                            component: models.value.mainChild.instance
                        )
                    }
                }
                .transition(AnyTransition .opacity.animation(.smooth))
            }
            
        }
        .viewBackground(
            color: MR.colors.shared.backgroundColor.getUIColor()
        )
        .onAppear {
            focus = false
        }
        .onChange(of: focus) { newValue in
            component.list(open: newValue)
        }
        .toolbar(content: {
            ToolbarItem(placement: .principal) {
                Text(models.value.searchListChild?.instance != nil ? "Search" : "Chats")
                    .font(.system(size: 18, weight: .medium))
                    .foregroundStyle(.primary)
            }
        })
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarBackButtonHidden()
    }
}

struct SearchListCell: View {
    
    let name: String
    
    var body: some View {
        VStack {
            HStack {
                Circle()
                    .fill(Color.gray.opacity(0.3))
                    .frame(height: 37)
                
                Text(name)
            }
            .frame(maxWidth: .infinity, maxHeight: 40, alignment: .leading)
            .padding(.horizontal, 8)
            
            Divider()
        }
    }
}

//#Preview {
//    SearchListView()
//}
