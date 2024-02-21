//
//  ChatsView.swift
//  iosApp
//
//  Created by Greg Zenkov on 1/12/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import SharedModule

struct ChatsView: View {
    
    let component: Chats
    
    @ObservedObject
    private var models: ObservableValue<ChatsStoreChatsUiState>
    
    
    init(
        component: Chats
    ) {
        self.component = component
        self.models = ObservableValue(component.chats)
    }
    
    var body: some View {
        VStack {
            if let error = models.value.errorMessage {
                Text(error)
            } else {
                ScrollView(.vertical) {
                    LazyVStack(spacing: 0) {
                        ForEach(models.value.chats, id: \.self) { chat in
                            ChatCell(name: chat.name)
                                .onTapGesture {
                                    component.openChat(chatUnit: chat)
                                }
                        }
                    }
                }
            }
        }
        .viewBackground(
            color: MR.colors.shared.backgroundColor.getUIColor()
        )
    }
}
