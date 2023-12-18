//
//  CardInfoView.swift
//  iosApp
//
//  Created by Greg Zenkov on 11/19/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import AVFoundation

final class AudioRecorder: NSObject, ObservableObject {
    private var recordingSession: AVAudioSession!
    private var whistleRecorder: AVAudioRecorder!
    private var timer: Timer?
    
    @Published var peek: Double = 0
    
    override init() {
        super.init()
        requestPermission()
        setupRecordSession()
    }
    
    private func requestPermission() {
        recordingSession = AVAudioSession.sharedInstance()
        
        do {
            try recordingSession.setCategory(.playAndRecord)
            try recordingSession.setActive(true)
            recordingSession.requestRecordPermission { _ in }
        } catch {
            
        }
    }
    
    func startRecord() {
        if whistleRecorder.isRecording == true {
            timer?.invalidate()
            timer = nil
            whistleRecorder.stop()
        } else {
            timer = Timer.scheduledTimer(timeInterval: 0.5, target: self, selector: #selector(update), userInfo: nil, repeats: true)
            whistleRecorder.record()
        }
    }
    
    private func setupRecordSession() {
        let settings = [
                AVFormatIDKey: Int(kAudioFormatMPEG4AAC),
                AVSampleRateKey: 12000,
                AVNumberOfChannelsKey: 1,
                AVEncoderAudioQualityKey: AVAudioQuality.high.rawValue
            ]
        
        self.whistleRecorder = try? AVAudioRecorder(url: getWhistleURL(), settings: settings)
        self.whistleRecorder.delegate = self
        self.whistleRecorder.isMeteringEnabled = true
        self.whistleRecorder.prepareToRecord()
    }
    
    private func getDocumentsDirectory() -> URL {
        let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        let documentsDirectory = paths[0]
        return documentsDirectory
    }
    
    private func getWhistleURL() -> URL {
        return getDocumentsDirectory().appendingPathComponent("whistle.m4a")
    }
    
    @objc private func update() {
        if whistleRecorder.isRecording == true {
            whistleRecorder.updateMeters()
            self.peek = Double(whistleRecorder.peakPower(forChannel: 0))
        }
    }
}

extension AudioRecorder: AVAudioRecorderDelegate {
    func audioRecorderDidFinishRecording(_ recorder: AVAudioRecorder, successfully flag: Bool) {
        print(flag)
    }
    
    func audioRecorderEncodeErrorDidOccur(_ recorder: AVAudioRecorder, error: Error?) {
        print(error)
    }
}

struct AnimView: View {
    
    @State private var firstCX: CGFloat = .zero
    @State private var firstCY: CGFloat = .zero
    @State private var secCX: CGFloat = .zero
    @State private var secCY: CGFloat = .zero
    
    @StateObject var recoreder = AudioRecorder()
    
    var body: some View {
        GeometryReader { geo in
            let size = geo.size
            Path { path in
                path.move(to: .init(x: 0, y: size.height / 2))
                path.addCurve(
                    to: .init(x: size.width, y: size.height / 2),
                    control1: .init(x: firstCX - CGFloat(recoreder.peek), y: firstCY),
                    control2: .init(x: secCX, y: secCY)
                )
            }
            
            VStack {
                Button {
                    recoreder.startRecord()
                } label: {
                    Text("Record")
                }

            }
        }
//        .onReceive(recoreder.$peek, perform: { peek in
//            /*@START_MENU_TOKEN@*//*@PLACEHOLDER=code@*/ /*@END_MENU_TOKEN@*/
//        })
//        .onAppear {
//            recoreder.requestPermission()
//        }
    }
}

#Preview {
    AnimView()
}
