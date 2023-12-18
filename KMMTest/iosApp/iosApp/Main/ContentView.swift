import SwiftUI
import SharedModule
import Combine
import ActivityKit

struct SomeView: View {
    @EnvironmentObject var coordinator: FlowCoordinator
    var body: some View {
        Button("Some") {
            coordinator.presenrThird()
        }
        //Text("dsddsdsds")
    }
}

struct SomeView1: View {
    var body: some View {
        Text("Hello")
    }
}

final class MainViewModelWrapper: ObservableObject {
    
    private let mainViewModel: MainViewModel
    
    @Published var posts: [Post]?
    @Published var isLoading = false
    
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
            }
            .store(in: &canc)
    }
    
    func onDisappear() {
        canc.forEach { $0.cancel() }
    }
}

@available(iOS 16.2, *)
struct ContentView: View {
    
    public init() {}
    
    @EnvironmentObject var coodrinator: FlowCoordinator
    @StateObject private var wrapper = MainViewModelWrapper()
    
    var body: some View {
        if let posts = wrapper.posts {
            List {
                ForEach(posts, id: \.self) { post in
                    VStack {
                        Text(post.title)
                            .font(.title2)
                        
                        Text(post.body)
                            .font(.body)
                    }
                }
            }
            .onDisappear {
                wrapper.onDisappear()
            }
        } else if wrapper.isLoading {
            ProgressView()
                .progressViewStyle(.circular)
        }
    }
}
