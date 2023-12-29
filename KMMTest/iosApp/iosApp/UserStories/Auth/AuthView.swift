//
//  AuthView.swift
//  iosApp
//
//  Created by Greg Zenkov on 12/24/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Combine
import SwiftUI
import SharedModule
import Stinsen
import UIElements

extension AppTheme {
    var scheme: ColorScheme? {
        switch self {
        case .systemDefault:
            return nil
        case .dark:
            return .dark
        case .light:
            return .light
        }
    }
}

struct AuthView: View {
    
    enum AuthTextFieldState {
        case nickname
        case phoneNumber
        case password
    }
    
    @FocusState private var focus: AuthTextFieldState?
    @StateObject private var wrapper = AuthViewModelWrapper()
    @Environment(\.colorScheme) var colorScheme
    @State private var toggleAppThemeView: Bool = false
    
    var body: some View {
        NavBar(title: "Hello", scheme: colorScheme, onTapLeadingButton: {
            withAnimation(.snappy) {
                toggleAppThemeView.toggle()
            }
        }) {
            ZStack {
                AnimateGradientView()
                AppThemSwitcherView(
                    toggle: $toggleAppThemeView,
                    colorScheme: colorScheme
                )
                
                VStack(spacing: 26) {
                    Spacer()
                    Text("Authorization")
                        .font(
                            .system(size: 36, weight: .semibold, design: .rounded)
                        )
                        .foregroundStyle(Color.blue)
                    
                    if let error = wrapper.error {
                        Text(error)
                            .foregroundStyle(.red)
                    }
                    
                    StateableTextFiledView(
                        text: $wrapper.nicknameState.text,
                        isError: $wrapper.nicknameState.isError,
                        isValid: $wrapper.nicknameState.isValid,
                        header: "Nickname",
                        onTypingListener: wrapper.onNicknameChanged
                    )
                    .focused($focus, equals: .nickname)
                    
                    StateableTextFiledView(
                        text: $wrapper.phoneNumberState.text,
                        isError: $wrapper.phoneNumberState.isError,
                        isValid: $wrapper.phoneNumberState.isValid,
                        header: "Email",
                        onTypingListener: wrapper.onPhoneNumber
                    )
                    .focused($focus, equals: .phoneNumber)
                    
                    StateableTextFiledView(
                        text: $wrapper.passwordField.text,
                        isError: $wrapper.passwordField.isError,
                        isValid: $wrapper.passwordField.isValid,
                        header: "Password",
                        onTypingListener: wrapper.onPasswordChanged
                    )
                    .focused($focus, equals: .password)
                    
                    Button {
                        wrapper.trySignUp()
                    } label: {
                        HStack {
                            Text("Start")
                                .font(.system(size: 20, weight: .semibold))
                                .foregroundStyle(.white)
                        }
                        .frame(maxWidth: .infinity, maxHeight: 56)
                        .background(wrapper.isValid ? Color.blue : Color.gray)
                        .clipShape(RoundedRectangle(cornerRadius: 10))
                    }
                    .disabled(!wrapper.isValid)
                    
                    Spacer()
                }
                .padding(.horizontal, 46)
                .onSubmit {
                    switch focus {
                    case .nickname:
                        focus = .phoneNumber
                    case .phoneNumber:
                        focus = .password
                    default:
                        focus = nil
                    }
                }
            }
        }
        .preferredColorScheme(colorScheme)
        .onAppear {
            wrapper.trySingInWithToken()
            focus = .nickname
        }
        .onDisappear {
            focus = nil
            wrapper.onDisapper()
        }
    }
}

private extension AuthView {
    final class AuthViewModelWrapper: ObservableObject {
        
        private let viewModel: AuthViewModel
        private var cancellables = Set<AnyCancellable>()
        
        @Published var nicknameState: AuthStoreField = .empty
        @Published var phoneNumberState: AuthStoreField = .empty
        @Published var passwordField: AuthStoreField = .empty
        @Published var isValid = false
        @Published var error: String? = nil
        var router: DefaultAuthCoordinator.Router? = RouterStore.shared.retrieve()
        
        init(
            viewModel: AuthViewModel = IosMainDI().authViewModel()
        ) {
            self.viewModel = viewModel
            bindUI()
        }
        
        func onNicknameChanged(text: String) {
            viewModel.acceptNickname(text: text)
        }
        
        func onPhoneNumber(text: String) {
            viewModel.acceptPhoneNumber(text: text)
        }
        
        func onPasswordChanged(text: String) {
            viewModel.acceptPassword(text: text)
        }
        
        func trySingInWithToken() {
            viewModel.trySingInWithToken()
        }
        
        func trySignUp() {
            viewModel.trySignUp()
        }
        
        func onDisapper() {
            cancellables.forEach { $0.cancel() }
            cancellables = []
        }
        
        private func bindUI() {
            FlowPublisher<AuthStoreUIAuthState>(flow: viewModel.state)
                .receive(on: DispatchQueue.main)
                .sink { [weak self] state in
                    withAnimation(.easeInOut) {
                        self?.nicknameState = state.nickname
                        self?.phoneNumberState = state.phoneNumberState
                        self?.passwordField = state.passwordState
                        self?.isValid = state.isButtonEnabled
                        self?.error = state.errorMessage
                    }
                      
                    if state.isSuccess {
                        self?.router?.coordinator.openMain()
                    }
                }
                .store(in: &cancellables)
        }
    }
}

#Preview {
    AuthView()
}

extension AuthStoreField {
    static let empty = AuthStoreField(text: "", isError: false, isValid: false)
}
