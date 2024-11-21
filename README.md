## Swear Detector

This project features an on-device swear detection algorithm that uses a 1D CNN to identify Korean swear words typed by users. Typing data is collected via Android's accessibility service but is **never sent to any server**, ensuring user privacy.

The goal of this project is to help users, especially teenagers, break the habit of typing swear words unconsciously.

Unlike traditional swear detection algorithms that rely on static dictionaries, this approach can detect variations such as "씨발" written as "^^ㅣ발."

### Training

The training dataset was collected by crawling online community sites and was manually labeled.
The model was trained and converted to an on-device format using **TensorFlow** and **TFLite**.
For detailed training information, check out model/train.ipynb.

### Screenshots

<img src="https://imgur.com/0MIMId3.png" width="200"><img src="https://imgur.com/sfYKEvu.png" width="200"><img src="https://imgur.com/rC6TsNF.png" width="200">
