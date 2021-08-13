//
// Created by ydl on 8/13/21.
//

#ifndef NDKAPPLICATION_DECODER_H
#define NDKAPPLICATION_DECODER_H


typedef void (*MessageCallback)(void*,int,float);
typedef void (*AVSyncCallback)(void*);

class Decoder{
public:
    //virtual void Start() = 0;
    virtual void Start()= 0;
    virtual void Pause() = 0;
    virtual void Stop() = 0;
    virtual float GetDuration() = 0;
    virtual void SeekToPosition(float position) = 0;
    virtual float GetCurrentPosition() = 0;
    virtual void SetMessageCallback(void* context, MessageCallback callback) = 0;

};


#endif //NDKAPPLICATION_DECODER_H
