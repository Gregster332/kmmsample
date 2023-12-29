import SwiftUI
import SharedModule
import Combine
import ActivityKit

final class MainViewModelWrapper: ObservableObject {
    
    private let mainViewModel: MainViewModel
    
    @Published var posts: [Post]?
    @Published var isLoading = false
    @Published var messages: [WsMessage] = []
    
    private var canc = Set<AnyCancellable>()
    
    init(mainViewModel: MainViewModel = IosMainDI().mainViewModel()) {
        self.mainViewModel = mainViewModel
        bindUI()
    }
    
    private func bindUI() {
        FlowPublisher<UIMainState>(flow: mainViewModel.state)
            .receive(on: RunLoop.main)
            .sink { [weak self] value in
                guard let self = self else { return }
                self.isLoading = value.isLoading
                self.posts = value.posts
                self.messages = value.messages
            }
            .store(in: &canc)
    }
    
    func onDisappear() {
        canc.forEach { $0.cancel() }
    }
    
    func sendMessage(message: String) {
        mainViewModel.tapSendMessage(message: message)
    }
}

struct MainView: View {
    
    @FocusState private var keyboardFocuse
    @SwiftUI.State private var text = ""
    //@EnvironmentObject var coodrinator: FlowCoordinator
    @StateObject private var wrapper = MainViewModelWrapper()
//    @State private var messages = [
//    WsMessage(userId: "0", userName: "Nick", text: "2121"),
//    WsMessage(userId: "1", userName: "Jack", text: "Hello"),
//    WsMessage(userId: "0", userName: "Nick", text: "a"),
//    WsMessage(userId: "1", userName: "Jack", text: "sds dsds aasdasd dsds dfdfd gfgf sds dsd fsf"),
//    WsMessage(userId: "0", userName: "Nick", text: "vb"),
//    WsMessage(userId: "1", userName: "Jack", text: "olld ppgr jsdg ofdj osdi csw"),
//    WsMessage(userId: "0", userName: "Nick", text: "8vg  83673y hcd88 fhud721"),
//    WsMessage(userId: "1", userName: "Jack", text: "adsio"),
//    WsMessage(userId: "0", userName: "Nick", text: "iii"),
//    ]
    
    var body: some View {
        VStack(spacing: 0) {
            ScrollView {
                LazyVStack(spacing: 0) {
                    ForEach(wrapper.messages, id: \.self) { message in
                        MessageView(message: message)
                            .rotationEffect(.radians(.pi))
                    }
                }
            }
            .rotationEffect(.radians(.pi))
            
            inputView()
        }
        .onDisappear {
            wrapper.onDisappear()
        }
    }
    
    @ViewBuilder
    private func inputView() -> some View {
        HStack {
            TextField("", text: $text)
                .focused($keyboardFocuse)
                .foregroundStyle(.white)
                .padding(6)
                .background(Color.black.opacity(0.7))
                .clipShape(Capsule())
            
            Button {
                wrapper.sendMessage(message: text)
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

#Preview {
    MainView()
        
}
