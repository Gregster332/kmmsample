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
    
    private let component: SignUpComponent
    
    @ObservedObject
    private var models: ObservableValue<SignUpStoreUISignUpState>
    
    init(component: SignUpComponent) {
        self.component = component
        self.models = ObservableValue(component.state)
    }
    
    var body: some View {
        VStack(spacing: 26) {
            Spacer()
            
            if let error = models.value.errorMessage {
                Text(error)
                    .foregroundStyle(.red)
            }
            
            StateableTextFiledView(
                text: .init(
                    get: { models.value.nickname.text },
                    set: { component.validateNickname(text: $0) }
                ),
                isError: .init(
                    get: { models.value.nickname.isError },
                    set: { models.value.nickname.isError = $0 }
                ),
                isValid: .init(
                    get: { models.value.nickname.isValid },
                    set: { models.value.nickname.isValid = $0 }
                ),
                colors: StateableTextFiledView.Colors(
                    appThemeBackgroundColor: MR.colors().textFieldBGColor.getUIColor(),
                    errorBackgroudColor: MR.colors().errorStateMainColor.getUIColor(),
                    successBackgroudColor: MR.colors().successStateMainColor.getUIColor()
                ),
                header: MR.strings().nickname_field_title.desc().localized()
            )
            .focused($focus, equals: .nickname)
            
            StateableTextFiledView(
                text: .init(
                    get: { models.value.phoneNumberState.text },
                    set: { component.validatePhone(text: $0) }
                ),
                isError: .init(
                    get: { models.value.phoneNumberState.isError },
                    set: { models.value.phoneNumberState.isError = $0 }
                ),
                isValid: .init(
                    get: { models.value.phoneNumberState.isValid },
                    set: { models.value.phoneNumberState.isValid = $0 }
                ),
                colors: StateableTextFiledView.Colors(
                    appThemeBackgroundColor: MR.colors().textFieldBGColor.getUIColor(),
                    errorBackgroudColor: MR.colors().errorStateMainColor.getUIColor(),
                    successBackgroudColor: MR.colors().successStateMainColor.getUIColor()
                ),
                header: MR.strings().phone_field_title.desc().localized()
            )
            .focused($focus, equals: .phoneNumber)
            
            StateableTextFiledView(
                text: .init(
                    get: { models.value.passwordState.text },
                    set: { component.validatePassword(text: $0) }
                ),
                isError: .init(
                    get: { models.value.passwordState.isError },
                    set: { models.value.passwordState.isError = $0 }
                ),
                isValid: .init(
                    get: { models.value.passwordState.isValid },
                    set: { models.value.passwordState.isValid = $0 }
                ),
                colors: StateableTextFiledView.Colors(
                    appThemeBackgroundColor: MR.colors().textFieldBGColor.getUIColor(),
                    errorBackgroudColor: MR.colors().errorStateMainColor.getUIColor(),
                    successBackgroudColor: MR.colors().successStateMainColor.getUIColor()
                ),
                header: MR.strings().password_field_title.desc().localized()
            )
            .focused($focus, equals: .password)
            
            Spacer()
            
            Button {
                component.authWithPassword()
            } label: {
                HStack {
                    Text("Start")
                        .font(.system(size: 20, weight: .semibold))
                        .foregroundStyle(.white)
                }
                .frame(maxWidth: .infinity, maxHeight: 56)
                .background(
                    models.value.nickname.isValid &&
                    models.value.phoneNumberState.isValid &&
                    models.value.passwordState.isValid
                    ? Color.blue : Color.gray
                )
                .clipShape(RoundedRectangle(cornerRadius: 10))
            }
            .padding(.bottom, 8)
            .disabled(
                !models.value.nickname.isValid &&
                !models.value.phoneNumberState.isValid &&
                !models.value.passwordState.isValid
            )
            .ignoresSafeArea(.keyboard, edges: .bottom)

        }
        .padding(.horizontal, 16)
        .viewBackground(
            color: MR.colors.shared.backgroundColor.getUIColor()
        )
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
        .onAppear {
            focus = .nickname
        }
        .onDisappear {
            focus = nil
        }
        .toolbar {
            ToolbarItem(placement: .principal) {
                Text(MR.strings().auth_screen_title.desc().localized())
                    .font(.title3.weight(.semibold))
            }
        }
        .navigationBarBackButtonHidden()
    }
}
