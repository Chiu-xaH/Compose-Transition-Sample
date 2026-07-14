import UIKit
import SwiftUI
import SharedNav

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        let cornerRadius = UIScreen.main.value(forKey: "displayCornerRadius") as? CGFloat ?? 0
        return MainViewControllerKt.MainViewController(cornerRadius: Float(cornerRadius))
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
                .ignoresSafeArea() // Compose has own keyboard handler
    }
}



