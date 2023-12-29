//
//  StateableTextFiledView.swift
//  UIElements
//
//  Created by Greg Zenkov on 12/27/23.
//

import SwiftUI

public struct StateableTextFiledView: View {
    
    public enum FieldViewState {
        case success
        case error
        case notSet
    }
    
    @Binding public var state: FieldViewState
    public let header: String
    public let errorHint: String?
    public let successHint: String?
    
    
    @State private var hint: String? = nil
    @Environment(\.colorScheme) var colorScheme
    @State private var text = ""
    
    public init(
        state: Binding<FieldViewState>,
        header: String,
        errorHint: String? = nil,
        successHint: String? = nil
    ) {
        self._state = state
        self.header = header
        self.errorHint = errorHint
        self.successHint = successHint
    }
    
    public var body: some View {
        VStack {
            ZStack(alignment: .top) {
                //HStack {
                VStack(alignment: .leading, spacing: 5) {
                    Text(header)
                        .font(.system(size: 18, weight: .medium))
                        .foregroundStyle(colorScheme == .dark ? .white : .black)
                        .offset(x: 8)
                    
                    HStack(spacing: 8) {
                        TextField("", text: $text)
                            .padding(8)
                            .overlay {
                                RoundedRectangle(cornerRadius: 13)
                                    .stroke(borderColor, lineWidth: 3)
                            }
//                            .onChange(of: text) { newValue in
//                                withAnimation(.easeIn) {
//                                    if newValue.isEmpty {
//                                        state = .notSet
//                                    } else if !newValue.contains("a") {
//                                        state = .error
//                                    } else {
//                                        state = .success
//                                    }
//                                    
//                                    if state == .error {
//                                        hint = "Lorem Ipsum - это текст- govno, часто используемый в печати и вэб-дизайне"
//                                    } else {
//                                        hint = nil
//                                    }
//                                }
//                            }
                        
                        
                        if state == .error {
                            Image(systemName: "xmark")
                                .foregroundStyle(.red)
                                .transition(.opacity)
                                .padding(10)
                                .background {
                                    Circle()
                                        .fill(visualHintViewBG)
                                }
                        } else if state == .success {
                            Image(systemName: "checkmark")
                                .foregroundStyle(.green)
                                .transition(.opacity)
                                .padding(10)
                                .background {
                                    Circle()
                                        .fill(visualHintViewBG)
                                }
                        } else {
                            EmptyView()
                        }
                    }
                    .padding(.horizontal, 3)
                    
                    if let hint = hint {
                        Text(hint)
                            .font(.system(size: 16, weight: .regular))
                            .foregroundStyle(colorScheme == .dark ? .white : .black)
                            .offset(x: 8)
                            .padding(.horizontal, 3)
                    }
                }
                //.padding(.horizontal, 8)
                .padding(.horizontal, 8)
                .padding(.vertical, 12)
                .background {
                    RoundedRectangle(cornerRadius: 15)
                        .fill(backgroundColor)
                }
                //}
            }
            .fixedSize(horizontal: false, vertical: true)
            //.padding(.horizontal, 8)

        }
    }
    
    private var backgroundColor: Color {
        switch state {
        case .success:
            return .green.opacity(0.3)
        case .error:
            return .red.opacity(0.3)
        case .notSet:
            switch colorScheme {
            case .dark:
                return .black.opacity(0.2)
            case .light:
                return .gray.opacity(0.1)
            @unknown default:
                return .clear
            }
        }
    }
    
    private var borderColor: Color {
        switch state {
        case .success:
            return .green.opacity(0.9)
        case .error:
            return .red.opacity(0.9)
        case .notSet:
            return .gray.opacity(0.8)
        }
    }
    
    private var visualHintViewBG: Color {
        switch state {
        case .success:
            return .green.opacity(0.4)
        case .error:
            return .red.opacity(0.4)
        default:
            return .clear
        }
    }
}

#Preview {
    StateableTextFiledView(
        //state: .success,
        state: .constant(.error),
        header: "Name"
    )
}
