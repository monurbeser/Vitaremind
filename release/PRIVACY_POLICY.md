# Privacy Policy — VitaRemind

**Effective date:** 2026-04-11  
**Developer:** Mehmet Onur Beşer  
**Contact:** monurbeser@gmail.com  
**App:** VitaRemind (com.vitaremind.app)

---

## 1. Information We Collect

VitaRemind is designed with privacy as a core principle. **We do not collect, store, or transmit any personal health data to external servers.**

### Data stored locally on your device
- Daily water intake logs (date, amount in ml, timestamp)
- Medicine records (name, dosage, color, reminder times)
- Dose history (taken/skipped status)
- App preferences (daily goal, theme, reminder intervals)

All of the above data is stored in a local Room database on your device and is **never transmitted to us or any third party**.

### Data collected by third-party services

**Google AdMob**  
This app uses Google AdMob to display advertisements. AdMob may collect:
- Device advertising ID
- IP address
- Approximate location (derived from IP)
- App interaction data for ad targeting

AdMob's data collection is governed by [Google's Privacy Policy](https://policies.google.com/privacy).  
You can opt out of personalized ads via your device settings:  
*Settings → Google → Ads → Opt out of Ads Personalization*

---

## 2. How We Use Information

- **Water and medicine data** — used solely within the app to display your progress, send reminders, and calculate streaks. Never leaves your device.
- **Ad data** — collected by AdMob exclusively for ad delivery. We do not have access to this data.

---

## 3. Data Sharing

We **do not sell, trade, or otherwise transfer** your personal information to third parties.

The only data sharing that occurs is through AdMob's advertising SDK, as described above. We have no control over AdMob's data practices.

---

## 4. Data Retention

All health data (water logs, medicine records) is stored only on your device. You can delete all data at any time via:  
**Settings → Reset All Data**

Uninstalling the app permanently deletes all locally stored data.

---

## 5. Permissions

| Permission | Purpose |
|---|---|
| `POST_NOTIFICATIONS` | Send water and medicine reminder notifications |
| `RECEIVE_BOOT_COMPLETED` | Re-schedule reminders after device restart |
| `INTERNET` | Required by AdMob to load advertisements |
| `SCHEDULE_EXACT_ALARM` | Deliver medicine notifications at precise times |

---

## 6. Children's Privacy

VitaRemind is not directed at children under the age of 13. We do not knowingly collect personal information from children. AdMob ads displayed in this app are not targeted at children.

---

## 7. Security

Your health data is stored in a private, sandboxed database accessible only to VitaRemind. We implement standard Android security practices to protect your data.

---

## 8. Changes to This Policy

We may update this Privacy Policy from time to time. Changes will be posted at:  
`https://monurbeser.github.io/vitaremind/privacy`

The effective date at the top of this document will be updated accordingly.

---

## 9. Contact Us

If you have questions about this Privacy Policy, contact:

**Mehmet Onur Beşer**  
📧 monurbeser@gmail.com  
🌐 https://monurbeser.github.io/vitaremind/

---

## GitHub Pages Deployment (for developer)

To host this privacy policy at `https://monurbeser.github.io/vitaremind/`:

1. Create a GitHub repository: `monurbeser/vitaremind` (public)
2. Go to Settings → Pages → Source: Deploy from branch → main → /docs
3. Create `/docs/index.html` with a redirect to `/docs/privacy.html`
4. Create `/docs/privacy.html` containing this policy in HTML format
5. In Google Play Console, enter:
   `https://monurbeser.github.io/vitaremind/privacy`
   as the Privacy Policy URL

Alternatively, publish directly from the repo root with a simple HTML wrapper.
