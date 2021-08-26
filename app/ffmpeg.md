#基础概念
DTS：解码时间戳
PTS：显示时间戳
pkt_pts：帧的pts数值
pkt_pts_time：通过time_base计算出来的显示时间
AV_TIME_BASE：内部时间单位是 （基准）
AV_TIME_BASE_Q：AV_TIME_BASE的倒数。从它的定义能很清楚的看到这点。
#时间基转换公式
timestamp(ffmpeg内部时间戳) = AV_TIME_BASE * time(秒)
time(秒) = AV_TIME_BASE_Q * timestamp(ffmpeg内部时间戳)


