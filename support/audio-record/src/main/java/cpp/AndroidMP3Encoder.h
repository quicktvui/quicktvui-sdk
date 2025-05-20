
#include <jni.h>

extern "C"
{
void Java_tvkit_media_audio_mp3_AndroidMP3Encoder_close(JNIEnv *env, jclass type);

jint
Java_tvkit_media_audio_mp3_AndroidMP3Encoder_encode(JNIEnv *env, jclass type, jshortArray buffer_l_,
                                                    jshortArray buffer_r_, jint samples,
                                                    jbyteArray mp3buf_);

jint
Java_tvkit_media_audio_mp3_AndroidMP3Encoder_flush(JNIEnv *env, jclass type, jbyteArray mp3buf_);

void Java_tvkit_media_audio_mp3_AndroidMP3Encoder_init__IIIII(JNIEnv *env, jclass type,
                                                              jint inSampleRate,
                                                              jint outChannel, jint outSampleRate,
                                                              jint outBitrate, jint quality);
}