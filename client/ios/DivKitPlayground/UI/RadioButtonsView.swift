import SwiftUI

struct RadioButtonsView: View {

  let options: [String]

  @Binding var selected: String

  var body: some View {
    VStack{
      ForEach(options, id: \.self) { option in
        Button(action: {
          selected = option
        }) {
          HStack{
            Text(option)
              .font(ThemeFont.text)
              .foregroundColor(Color(UIColor.label))
            Spacer()
            Circle()
              .fill(selected == option ? Color(UIColor.systemGreen) : Color(UIColor.systemGray4))
              .frame(width: 20, height: 20)
          }
        }
      }
    }
  }
}
