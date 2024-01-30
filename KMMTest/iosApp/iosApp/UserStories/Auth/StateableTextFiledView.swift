//
//  StateableTextFiledView.swift
//  UIElements
//
//  Created by Greg Zenkov on 12/27/23.
//

import SwiftUI

public struct StateableTextFiledView: View {
    
    public struct Colors {
        let appThemeBackgroundColor: UIColor
        let errorBackgroudColor: UIColor
        let successBackgroudColor: UIColor
        
        public init(
            appThemeBackgroundColor: UIColor,
            errorBackgroudColor: UIColor,
            successBackgroudColor: UIColor
        ) {
            self.appThemeBackgroundColor = appThemeBackgroundColor
            self.errorBackgroudColor = errorBackgroudColor
            self.successBackgroudColor = successBackgroudColor
        }
        
        static public let defaulfValue = Colors(
            appThemeBackgroundColor: .gray,
            errorBackgroudColor: .gray,
            successBackgroudColor: .black
        )
    }
    
    @Binding var text: String
    @Binding var isError: Bool
    @Binding var isValid: Bool
    let header: String
    let colors: Colors
    let hint: String?
    
    @State private var showHint: Bool = false
    @Environment(\.colorScheme) var colorScheme
    
    public init(
        text: Binding<String>,
        isError: Binding<Bool>,
        isValid: Binding<Bool>,
        colors: Colors = Colors.defaulfValue,
        header: String,
        hint: String? = nil
    ) {
        self._text = text
        self._isError = isError
        self._isValid = isValid
        self.header = header
        self.colors = colors
        self.hint = hint
    }
    
    public var body: some View {
        VStack {
            ZStack(alignment: .top) {
                VStack(alignment: .leading) {
                    Text(header)
                        .font(.system(size: 18, weight: .medium))
                        .foregroundStyle(colorScheme == .dark ? .white : .black)
                        .offset(x: 8)
                    
                    HStack(spacing: 8) {
                        TextField("", text: $text)
                            .padding(8)
                        
                        Group {
                            if isError {
                                CircleHint(.failure, borderColor)
                            } else if isValid {
                                CircleHint(.success, borderColor)
                            } else {
                                EmptyView()
                            }
                        }
                        .frame(height: 40)
                    }
                    .overlay {
                        RoundedRectangle(cornerRadius: 25)
                            .stroke(
                                borderColor,
                                lineWidth: 3
                            )
                    }
                    
                    if isError || isValid, let hint = hint {
                        Text(hint)
                            .font(.system(size: 16, weight: .regular))
                            .foregroundStyle(colorScheme == .dark ? .white : .black)
                            .lineLimit(2)
                            .padding(.horizontal, 8)
                            .transition(.opacity)
                    }
                }
            }
            .fixedSize(horizontal: false, vertical: true)
        }
    }
    
    private var borderColor: Color {
        colors.appThemeBackgroundColor.toColor()
    }
}

#Preview {
    StateableTextFiledView(
        text: .constant(""),
        isError: .constant(false),
        isValid: .constant(true),
        header: "Name",
        hint: ""
    )
}

extension UIColor {
    func toColor() -> Color {
        Color(self)
    }
}
