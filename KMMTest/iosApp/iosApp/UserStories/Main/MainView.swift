import SwiftUI
import SharedModule
import Combine
import ActivityKit

struct MainView: View {
    
    @FocusState private var keyboardFocuse
    @State private var isPresentedCreate = false
    @State private var lastCaretedChatName = ""
    
    let component: Chat
    
    @ObservedObject
    private var models: ObservableValue<ChatStoreState>
    
    init(component: Chat) {
        self.component = component
        self.models = ObservableValue(component.currentMessages)
    }
    
    var body: some View {
        VStack(spacing: 0) {
            if !models.value.messages.isEmpty {
                ScrollView {
                    LazyVStack(spacing: 0) {
                        ForEach(models.value.messages, id: \.self) { message in
                            MessageView(chatMessage: message)
                                .rotationEffect(.radians(.pi))
                        }
                    }
                }
                .rotationEffect(.radians(.pi))
            } else {
                Text("Empty message :(")
                    .frame(maxHeight: .infinity)
            }
            
            inputView()
        }
        .viewBackground(
            color: MR.colors.shared.backgroundColor.getUIColor()
        )
        .onDisappear {
            component.close()
        }
        .toolbar(content: {
            ToolbarItem(placement: .principal) {
                Text("Chat")
                    .font(.system(size: 18, weight: .medium))
                    .foregroundStyle(.primary)
            }
        })
        .navigationBarTitleDisplayMode(.inline)
    }
    
    @ViewBuilder
    private func inputView() -> some View {
        HStack {
            TextField("", text: $lastCaretedChatName)
                .focused($keyboardFocuse)
                .foregroundStyle(.white)
                .padding(6)
                .background(Color.black.opacity(0.7))
                .clipShape(Capsule())
            
            Button {
                component.send(message: lastCaretedChatName)
            } label: {
                Image(systemName: "arrow.up")
                    .font(.system(size: 23))
                    .padding(6)
                    .background(Color.blue.opacity(0.2))
                    .clipShape(Capsule(style: .circular))
            }

        }
        .padding(8)
        .background {
            Color.black.opacity(0.7)
                .ignoresSafeArea(edges: .bottom)
        }
    }
}

//#Preview {
//    MainView()
//        
//}
