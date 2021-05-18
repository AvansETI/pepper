import { TestBed } from '@angular/core/testing';

import { MessageEncryptorService } from './message-encryptor.service';

describe('MessageEncryptorServiceService', () => {
  let service: MessageEncryptorService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MessageEncryptorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
