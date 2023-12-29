//
//  StateableTextFiledView.swift
//  UIElements
//
//  Created by Greg Zenkov on 12/27/23.
//

import SwiftUI
import UIElements

public struct StateableTextFiledView: View {
    
    public enum FieldViewState {
        case success
        case error
        case notSet
    }
    
    @Binding var text: String
    @Binding var isError: Bool
    @Binding var isValid: Bool
    let hint: String?
    public let header: String
    public let onTypingListener: (String) -> Void
    
    @State private var showHint: Bool = false
    @Environment(\.colorScheme) var colorScheme
    
    public init(
        text: Binding<String>,
        isError: Binding<Bool>,
        isValid: Binding<Bool>,
        hint: String? = nil,
        header: String,
        onTypingListener: @escaping (String) -> Void
    ) {
        self._text = text
        self._isError = isError
        self._isValid = isValid
        self.header = header
        self.hint = hint
        self.onTypingListener = onTypingListener
    }
    
    public var body: some View {
        VStack {
            ZStack(alignment: .top) {
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
                            .onChange(of: text) { newValue in
                                onTypingListener(newValue)
                            }

                        if isError {
                            CircleHint(
                                "xmark",
                                .red,
                                visualHintViewBG
                            )
                        } else if isValid {
                            CircleHint(
                                "checkmark",
                                .green,
                                visualHintViewBG
                            )
                        } else {
                            EmptyView()
                        }
                    }
                    .padding(.horizontal, 3)
                    
                    if isError || isValid, let hint = hint {
                        Text(hint)
                            .font(.system(size: 16, weight: .regular))
                            .foregroundStyle(colorScheme == .dark ? .white : .black)
                            .lineLimit(2)
                            
                            .padding(.horizontal, 8)
                            .transition(.opacity)
                    }
                }
                .padding(.horizontal, 8)
                .padding(.vertical, 12)
                .background {
                    RoundedRectangle(cornerRadius: 15)
                        .fill(backgroundColor)
                }
            }
            .fixedSize(horizontal: false, vertical: true)
        }
    }
    
    private var backgroundColor: Color {
        if isValid {
            return .green.opacity(0.3)
        } else if isError {
            return .red.opacity(0.3)
        } else {
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
        if isValid {
            return .green.opacity(0.9)
        } else if isError {
            return .red.opacity(0.9)
        } else {
            return .gray.opacity(0.8)
        }
    }
    
    private var visualHintViewBG: Color {
        if isValid {
            return .green.opacity(0.4)
        } else if isError {
            return .red.opacity(0.4)
        } else {
            return .clear
        }
    }
}

#Preview {
    StateableTextFiledView(
        text: .constant(""),
        isError: .constant(false),
        isValid: .constant(true),
        hint: "",
        header: "Name",
        onTypingListener: {_ in }
    )
}
