import { Injectable } from '@angular/core';
import { base64 } from "rfc4648";
import { sha256 } from 'js-sha256';

@Injectable({
  providedIn: 'root'
})
export class MessageEncryptorService {

  private static readonly ENCRYPT_ALGO = 'AES-GCM';
  private static readonly TAG_LENGTH = 128;
  private static readonly IV_LENGTH = 12;

  private encoder;
  private decoder;

  constructor() { 
    this.encoder = new TextEncoder()
    this.decoder = new TextDecoder()
  }

  async encrypt (message: string, password: string): Promise<string> {
    const encoded = this.encoder.encode(message);
    const iv = crypto.getRandomValues(new Uint8Array(MessageEncryptorService.IV_LENGTH));
    const keyHash = sha256.arrayBuffer(password);

    const key = await crypto.subtle.importKey(
      'raw',
      keyHash,
      MessageEncryptorService.ENCRYPT_ALGO,
      false,
      ['encrypt', 'decrypt']
    );

    const cipherText = await crypto.subtle.encrypt(
      {
        name: MessageEncryptorService.ENCRYPT_ALGO,
        iv: iv,
        tagLength: MessageEncryptorService.TAG_LENGTH
      },
      key,
      encoded
    );

    const result = this.concat(iv.buffer, cipherText);

    return base64.stringify(new Uint8Array(result));
  }

  async decrypt (message: string, password: string): Promise<string> {
    const decoded = base64.parse(message);
    const iv = decoded.subarray(0, MessageEncryptorService.IV_LENGTH);
    const cipherText = decoded.subarray(MessageEncryptorService.IV_LENGTH, decoded.length);
    const keyHash = sha256.arrayBuffer(password);

    const key = await crypto.subtle.importKey(
      'raw',
      keyHash,
      MessageEncryptorService.ENCRYPT_ALGO,
      false,
      ['encrypt', 'decrypt']
    );

    const decrypted = await crypto.subtle.decrypt(
      {
        name: MessageEncryptorService.ENCRYPT_ALGO,
        iv: iv,
        tagLength: MessageEncryptorService.TAG_LENGTH
      },
      key,
      cipherText
    );

    return this.decoder.decode(decrypted);
  }

  concat(buffer1: ArrayBuffer, buffer2: ArrayBuffer): ArrayBuffer {
    const tmp = new Uint8Array(buffer1.byteLength + buffer2.byteLength);
    tmp.set(new Uint8Array(buffer1), 0);
    tmp.set(new Uint8Array(buffer2), buffer1.byteLength);
    return tmp.buffer;
  }

  hash(text: string): string {
    return sha256.hex(text);
  }

}
