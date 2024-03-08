//
//  MessageView.swift
//  iosApp
//
//  Created by Greg Zenkov on 12/23/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import SharedModule

struct MessageView: View {
    let chatMessage: ChatStoreChatMessage
    
    var body: some View {
        HStack {
            if chatMessage.isOutgoing {
                Spacer()
            }
            
            HStack(alignment: .bottom, spacing: 4) {
                VStack(alignment: .leading, spacing: 6) {
                    Group {
                        if !chatMessage.isOutgoing {
                            Text(chatMessage.nickname)
                                .font(.system(size: 16, weight: .semibold))
                        }
                        
                        Text(chatMessage.messageText)
                            .font(.system(size: 15, weight: .medium))
                    }
                    .foregroundStyle(.white.opacity(0.9))
                }
                
                Text(chatMessage.date)
                    .font(.system(size: 10))
                    .foregroundStyle(MR.colors().textFieldBGColor.toSUIColor)
            }
            .padding(10)
            .background(chatMessage.isOutgoing ? Color.blue.opacity(0.9) : Color.black.opacity(0.7))
            .clipShape(RoundedRectangle(cornerRadius: 14))
            
            if !chatMessage.isOutgoing {
                Spacer()
            }
        }
        .padding(chatMessage.isOutgoing ? .trailing : .leading, 10)
        .padding(chatMessage.isOutgoing ? .leading : .trailing, 20)
        .padding(.bottom, 8)
    }
}

extension SharedModule.ColorResource {
    var toSUIColor: SwiftUI.Color {
        Color(self.getUIColor())
    }
}

//#Preview {
//    MessageView(isIncoming: true, nick: "Helo", text: "Message text here Message text here Message text here Message text here Message text here Message text here Message text here Message text here")
//}
