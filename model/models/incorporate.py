with open('normals.txt', 'a') as f_normal, open('swears.txt', 'a') as f_swear:
    # with open('dict.normals.txt', 'r') as f_in:
    #     for line in f_in:
    #         if line.strip().split('\t')[-1] == '1':
    #             f_swear.write(line)
    #         else:
    #             f_normal.write(line)
        
    with open('dict.swears.txt', 'r') as f_in:
        for line in f_in:
            if line.strip().split('\t')[-1] == '1':
                f_swear.write(line)
            else:
                f_normal.write(line)