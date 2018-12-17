//
// Created by 李志丹 on 2018/11/13.
//

#ifndef LANCELSN_SAFE_QUEUE_H
#define LANCELSN_SAFE_QUEUE_H


#include <queue>
#include <pthread.h>

using namespace std;

template<typename T>


class SafeQueue {
    typedef void (*ReleaseCallBack)(T *);

    typedef void (*SyncHandler)(queue<T> &);

public:

    SafeQueue() {
        pthread_mutex_init(&mutex, 0);
        pthread_cond_init(&cond, 0);
    }

    ~SafeQueue() {
        pthread_mutex_destroy(&mutex);
        pthread_cond_destroy(&cond);
    }

    void push(T t) {
        pthread_mutex_lock(&mutex);
        if (work) {
            q.push(t);
            //通知
            pthread_cond_signal(&cond);
        } else {
            if (releaseCallBack) {
                releaseCallBack(&t);
            }
        }
        pthread_mutex_unlock(&mutex);
    }

    int pop(T &t) {
        int ret = 0;
        pthread_mutex_lock(&mutex);
        while (work && q.empty()) {
            pthread_cond_wait(&cond, &mutex);
        }
        if (!q.empty()) {
            t = q.front();
            q.pop();
            ret = 1;
        }
        pthread_mutex_unlock(&mutex);
        return ret;
    }

    void clear() {
        pthread_mutex_lock(&mutex);
        uint32_t size = q.size();
        for (int i = 0; i < size; ++i) {
            T t = q.front();
            if (releaseCallBack) {
                releaseCallBack(&t);
            }
            q.pop();
        }
        pthread_mutex_unlock(&mutex);
    }

    void setReleaseCallBack(ReleaseCallBack releaseCallBack) {
        this->releaseCallBack = releaseCallBack;
    }

    void setSyncHandler(SyncHandler syncHandler) {
        this->syncHandler = syncHandler;
    }

    void sync() {
        pthread_mutex_lock(&mutex);
        syncHandler(q);
        pthread_mutex_unlock(&mutex);
    }

    void setWork(int work) {
        pthread_mutex_lock(&mutex);
        this->work = work;
        pthread_cond_signal(&cond);
        pthread_mutex_unlock(&mutex);
    }

    int empty() {
        return q.empty();
    }

    int size() {
        return q.size();
    }

private:
    //工作标记
    int work;
    pthread_cond_t cond;
    pthread_mutex_t mutex;
    queue<T> q;
    ReleaseCallBack releaseCallBack;
    SyncHandler syncHandler;
};

#endif //LANCELSN_SAFE_QUEUE_H
