# SafeFall Alerts 📱🚨

**SafeFall Alerts** is an intelligent Android application designed to detect accidental falls and send emergency SMS alerts with GPS location, especially useful for elderly individuals and high-risk users.

## 📌 Overview

Falls are a major health risk, particularly for the elderly. This app uses smartphone motion sensors to detect falls in real time and automatically alert emergency contacts with the user’s location.

---

## 🧠 Features

- **Automatic Fall Detection**  
  Uses accelerometer and gyroscope data to detect high-impact motions.

- **Manual SOS Trigger**  
  A one-tap red button for manual emergency activation.

- **Real-Time Location Tracking**  
  Sends GPS coordinates or Google Maps link to contacts.

- **Sensitivity Settings**  
  Adjustable thresholds to reduce false alerts or increase detection.

---

## 📸 Screenshots

| <img src="https://github.com/user-attachments/assets/b36067b9-9698-4de2-a3ce-6a61012ffa20" width="250"/> | <img src="https://github.com/user-attachments/assets/96ca56d7-1697-43b1-8554-248476beada6" width="250"/> | <img src="https://github.com/user-attachments/assets/689ce14e-1060-4fbc-a896-664b58477eb1" width="250"/> |
|:--:|:--:|:--:|
| *Main UI* | *Fall Detection Confirmation* | *Background Monitoring* |


## 📱 System Requirements

- **OS:** Android 11 (API 30) or higher  
- **Sensors:** Accelerometer + Gyroscope  
- **Permissions:** SMS, Location, Overlay, Background Activity

---

## ⚙️ Tech Stack

- **Language:** Kotlin  
- **IDE:** Android Studio  
- **APIs Used:**  
  - `SensorManager` for motion detection  
  - `FusedLocationProviderClient` for location  
  - `SMSManager` for alerts  

---

## 🛠️ Setup & Usage

1. **Install APK**  
   Enable “Unknown Sources” and install the APK.

2. **Launch App**  
   Complete initial setup with emergency contact and sensitivity settings.

3. **Run in Background**  
   The app monitors continuously with low battery usage.

4. **Emergency Protocol**  
   - On fall detection: Full-screen confirmation appears  
   - If unconfirmed: SMS alert sent with GPS location  
   - Manual trigger also available

---

## ⚠️ Known Limitations

- May produce false positives during high movement (e.g., sports)
- Requires the device to be body-mounted
- Battery must be ≥ 20% for optimal reliability

---

## 🚀 Future Enhancements

- **Voice Command Support** (v2.0)  
  Emergency cancellation via voice (e.g., “I'm OK”)

- **Wearable Integration** (v3.0)  
  Smartwatch support for better detection and feedback

---

## 👨‍💻 Authors

- H.A.O.S. Amarasuriya (`200031A`)  
- A.A.D.S. Eranda (`200160R`)  

Project for **CS4473 - Mobile Computing**  
Team Name: **Team Xenon**

---

## 📄 License

This project is for academic purposes and currently not under an open-source license.
