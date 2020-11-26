from models.model import Model
from concurrent.futures import ThreadPoolExecutor

class Dict(Model):
    def __init__(self):
        with open('models/badWords.txt') as f_in:
            self.badwords = f_in.read().split('\n')
        self.normals = []
        self.swears = []

    def classify_one(self, text):
        swear_detected = False
        for badword in self.badwords:
            if badword in text:
                swear_detected = True
                break
        if swear_detected:
            self.swears.append(text)
        else:
            self.normals.append(text)
    
    def classify(self, texts):
        with ThreadPoolExecutor(max_workers=16) as executor:
            executor.map(self.classify_one, texts)
        self.write_to_files()
        
    def write_to_files(self):
        with open('models/dict.swears.txt', 'w') as f_out:
            for swear in self.swears:
                f_out.write(swear + '\t' + '1\n')
        with open('models/dict.normals.txt', 'w') as f_out:
            for normal in self.normals:
                f_out.write(normal + '\t' + '0\n')
            
            