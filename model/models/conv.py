from models.model import Model
from concurrent.futures import ThreadPoolExecutor
import tensorflow as tf
from jamo import h2j, j2hcj
import numpy as np

vocab = ['ㄷ', 'ㅏ', 'ㄴ', 'ㄱ', 'ㅜ', 'ㅎ', 'ㄹ', 'ㅇ', 'ㅂ', 'ㅓ', 'ㅈ', 'ㅣ', ' ',
 'ㅡ', 'ㅢ', 'ㅁ', 'ㅗ', 'ㅅ', 'ㅔ', 'ㅕ', 'ㅑ', ';', 'B', 'J', '.', 'P', 'G',
 'ㄸ', 'ㅟ', 'ㅃ', 'ㅌ', '[', '1', ':', '8', '2', '3', '0', ']', 'V', 'L',
 'I', 'E', 'ㅋ', 'ㅖ', '(', 'ㅠ', ')', '5', 'ㅝ', 'ㅐ', 'ㅆ', "'", 'ㅀ', 'ㅊ',
 't', 'x', 'ㅙ', 'ㅚ', 'ㅉ', 'ㅍ', 'ㅄ', '?', 'g', 'i', 'f', 'ㅛ', '6', '7', '☀',
 'ㄲ', 'v', 's', 'ㅘ', '!', 'ㄶ', 'p', 'c', 'ㄼ', '\u3000', 'k', '4', '9', ',',
 'ㅞ', 'ㅒ', '“', '”', 'N', '‘', '’', 'T', 'O', 'a', 'r', 'm', 'S', '+', 'o', 'd',
 'l', 'u', '·', '~', '/', 'ㄻ', '^', 'ㄺ', 'e', 'n', 'A', '-', 'D', '&', 'C',
 'F', 'j', 'M', 'K', '"', '_', 'Z', 'X', 'U', '…', 'ㄾ', 'w', '=', 'z',
 '>', '<', 'b', 'H', '@', '*', 'W', 'y', 'h', 'R', '%', 'ㄽ', '．',
 'ｊ', 'ｐ', 'ｇ', 'ㄵ', '{', '}', 'q', 'Y', 'Q',
 '$', 'ㄿ', '？', 'ㆍ', 'ㄳ', '⋅', '—']

vocab_dict = {c: i for i, c in enumerate(vocab, 1)}

UNK = 0
PAD = len(vocab_dict) + 1

def preprocessing(char_list):
    ret = [vocab_dict[char] if char in vocab_dict else UNK for char in char_list]
    if len(ret) <= 100:
        ret += [PAD] * (100 - len(ret))
    else:
        ret = ret[:100]
    return ret

class Conv(Model):
    def __init__(self):
        self.model = tf.keras.models.load_model('models/model')
        self.normals = []
        self.swears = []

    def classify_one(self, text):
        x = j2hcj(h2j(text))
        x = preprocessing(list(x))
        prediction = self.model.predict(np.array([x]))
        if prediction[0][0] <= prediction[0][1]:
            self.swears.append(text)
        else:
            self.normals.append(text)
    
    def classify(self, texts):
        with ThreadPoolExecutor(max_workers=16) as executor:
            executor.map(self.classify_one, texts)
        self.write_to_files()
        
    def write_to_files(self):
        with open('models/conv.swears.txt', 'w') as f_out:
            for swear in self.swears:
                f_out.write(swear + '\t' + '1\n')
        with open('models/conv.normals.txt', 'w') as f_out:
            for normal in self.normals:
                f_out.write(normal + '\t' + '0\n')
            
            