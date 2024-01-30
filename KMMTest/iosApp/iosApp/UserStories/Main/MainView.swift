import SwiftUI
import SharedModule
import Combine
import ActivityKit

final class MainViewModelWrapper: ObservableObject {
    
    //private let mainViewModel: MainViewModel
    
    @Published var isLoading = false
    @Published var messages: [WsMessage] = []
    @Published var chats: [ChatUnit] = []
    
    private var canc = Set<AnyCancellable>()
    
//    init(mainViewModel: MainViewModel = IosMainDI().mainViewModel()) {
//        self.mainViewModel = mainViewModel
//        bindUI()
//    }
    
    private func bindUI() {
//        FlowPublisher<UIMainState>(flow: mainViewModel.state)
//            .receive(on: RunLoop.main)
//            .sink { [weak self] value in
//                guard let self = self else { return }
//                self.isLoading = value.isLoading
//                self.messages = value.messages
//                self.chats = value.chats
//            }
//            .store(in: &canc)
    }
    
    func onDisappear() {
        canc.forEach { $0.cancel() }
    }
    
    func sendMessage(message: String) {
        //mainViewModel.tapSendMessage(message: message)
    }
    
    func createNewChat(name: String) {
        //mainViewModel.createNewChat(name: name)
    }
}

struct MainView: View {
    
    @FocusState private var keyboardFocuse
    @State private var isPresentedCreate = false
    @State private var lastCaretedChatName = ""
    @StateObject private var wrapper = MainViewModelWrapper()
    
    var body: some View {
        //            ScrollView {
        //                LazyVStack(spacing: 0) {
        //                    ForEach(wrapper.messages, id: \.self) { message in
        //                        MessageView(message: message)
        //                            .rotationEffect(.radians(.pi))
        //                    }
        //                }
        //            }
        //            .rotationEffect(.radians(.pi))
                    
                    //inputView()
        VStack(spacing: 0) {
            ScrollView {
                ForEach(wrapper.chats, id: \.self) { i in
                    ChatCell(name: i.name)
                }
            }

        }
        .onDisappear {
            wrapper.onDisappear()
        }
        .toolbar(content: {
            ToolbarItem(placement: .principal) {
                Text("Chats")
                    .font(.system(size: 18, weight: .medium))
                    .foregroundStyle(.primary)
            }
            
            ToolbarItem(placement: .topBarTrailing) {
                Button {
                    isPresentedCreate.toggle()
                } label: {
                    Text("Add")
                }
                .alert(
                    "Hello",
                    isPresented: $isPresentedCreate) {
                        TextField("dsds", text: $lastCaretedChatName)
                        Button("Send", action: {
                            wrapper.createNewChat(name: lastCaretedChatName)
                            isPresentedCreate.toggle()
                        })
                    } message: {
                        Text("Message here")
                    }
            }
        })
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarBackButtonHidden()
    }
    
//    @ViewBuilder
//    private func inputView() -> some View {
//        HStack {
//            TextField("", text: $text)
//                .focused($keyboardFocuse)
//                .foregroundStyle(.white)
//                .padding(6)
//                .background(Color.black.opacity(0.7))
//                .clipShape(Capsule())
//            
//            Button {
//                wrapper.sendMessage(message: text)
//            } label: {
//                Image(systemName: "arrow.up")
//                    .font(.system(size: 23))
//                    .padding(6)
//                    .background(Color.blue.opacity(0.2))
//                    .clipShape(Capsule(style: .circular))
//            }
//
//        }
//        .padding(8)
//        .background {
//            Color.black.opacity(0.7)
//                .ignoresSafeArea(edges: .bottom)
//        }
//    }
}

#Preview {
    MainView()
        
}
