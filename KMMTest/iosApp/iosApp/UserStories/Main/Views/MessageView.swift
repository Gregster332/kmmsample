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
    
    let userId = "0"
    let name: String
    let message: String
    
    var body: some View {
        HStack {
            
            if userId == "0" {
                Spacer()
            }
            
            VStack(alignment: .leading, spacing: 6) {
                Group {
                    Text(name)
                        .font(.system(size: 16, weight: .semibold))
                    Text(message)
                        .font(.system(size: 15, weight: .medium))
                }
                .foregroundStyle(.white.opacity(0.9))
            }
            .padding(10)
            .background(userId == "0" ? Color.blue.opacity(0.9) : Color.black.opacity(0.7))
            .clipShape(RoundedRectangle(cornerRadius: 14))
            
            if !(userId == "0") {
                Spacer()
            }
        }
        .padding(userId == "0" ? .trailing : .leading, 10)
        .padding(userId == "0" ? .leading : .trailing, 20)
        .padding(.bottom, 8)
    }
}

//#Preview {
//    MessageView(isIncoming: true, nick: "Helo", text: "Message text here Message text here Message text here Message text here Message text here Message text here Message text here Message text here")
//}
