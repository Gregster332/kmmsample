//
//  ChatCell.swift
//  iosApp
//
//  Created by Greg Zenkov on 1/5/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct ChatCell: View {
    
    let name: String
    
    var body: some View {
        VStack(spacing: 0) {
            Divider()
            HStack(alignment: .top) {
                HStack(spacing: 12) {
                    Circle()
                        .padding(4)
                    
                    VStack(alignment: .leading, spacing: 2) {
                        Text(name)
                            .font(.system(size: 16, weight: .medium))
                            .foregroundStyle(.primary)
                        
                        Text("Message Message. Message, Message, Message sf Message js Message")
                            .font(.system(size: 14, weight: .regular))
                            .foregroundStyle(.secondary)
                        
                        //Spacer()
                    }
                    //.padding(.top, 2)
                    
                    Spacer()
                }
                
                Text("15:48")
                    .font(.system(size: 16, weight: .regular))
                    .foregroundStyle(.secondary)
            }
            .padding(2)
            .frame(maxWidth: .infinity)
            .padding(.horizontal, 8)
        }
        .frame(height: 66)
    }
}

#Preview {
    ChatCell(name: "")
}
