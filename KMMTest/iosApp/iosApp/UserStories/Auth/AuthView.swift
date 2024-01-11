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
import UIElements

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
        ZStack {
            AnimateGradientView()
            
            VStack(spacing: 26) {
                Spacer()
                
                //if focus == nil {
                    Text("Authorization")
                        .font(
                            .system(
                                size: focus == nil ? 36 : 16,
                                weight: .semibold,
                                design: .rounded
                            )
                        )
                        .foregroundStyle(Color.blue)
                //}
                
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
        .ignoresSafeArea(.keyboard, edges: .bottom)
        .sheet(isPresented: $toggleAppThemeView, content: {
            AppThemSwitcherView(
                toggle: $toggleAppThemeView,
                colorScheme: colorScheme
            )
            .presentationDetents([.fraction(0.2)])
        })
        .onAppear {
            //wrapper.trySingInWithToken()
            focus = .nickname
        }
        .onDisappear {
            focus = nil
            wrapper.onDisapper()
        }
        .preferredColorScheme(colorScheme)
        .toolbar {
            ToolbarItem(placement: .principal) {
                VStack {
                    Text("Hello")
                        .font(.system(size: 16, weight: .medium))
                        .foregroundStyle(.primary)
                    Text("Subtitle")
                        .font(.system(size: 14, weight: .regular))
                        .foregroundStyle(.secondary)
                }
            }
            
            ToolbarItem(placement: .topBarLeading) {
                Button {
                    toggleAppThemeView.toggle()
                } label: {
                    Text("dsds")
                }
            }
        }
        .navigationBarBackButtonHidden()
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
                        Coordinator.shared.showMain()
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
