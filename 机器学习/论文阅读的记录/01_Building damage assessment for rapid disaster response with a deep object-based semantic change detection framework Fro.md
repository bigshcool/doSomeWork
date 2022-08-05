# Building damage assessment for rapid disaster response with a deep object-based semantic change detection framework: From natural disasters to man-made disasters
## 1.解决灾害评估问题的关键是

在建筑物损伤评估中，**强特征表示和语义一致性**是获得高准确率的关键。

## 2.现有解决方案的问题点

传统的基于对象的图像分析(OBIA)框架使用基于patch的卷积神经网络(CNN)可以保证语义的一致性，但特征表示较弱，而Siamese全卷积网络方法具有较强的特征表示能力，但语义不一致。

### 3.提出的解决方法

在本文中，我们提出了一个基于深度对象的语义变化检测框架，称为ChangeOS，用于建筑物损伤评估。为了实现OBIA和深度学习的无缝集成，我们采用了深度对象定位网络来生成精确的建筑对象，取代了传统OBIA框架中常用的超像素分割。在此基础上，将深度目标定位网络和深度损伤分类网络集成为统一的语义变化检测网络，实现对建筑物的端到端损伤评估。这还提供了深度对象特征，可以在深度损伤分类网络之前提供对象，以实现更一致的语义特征表示。采用基于对象的后处理进一步保证了各个对象语义的一致性。

### 4.