/**
 * Common methods to be used cross all components
 */
import {Timestamp} from 'google-protobuf/google/protobuf/timestamp_pb';

/**
 * Convert proto timestamp to readable string
 */
export function convertTimestamp(timestamp: Timestamp): string {
  const date = new Date(timestamp.getSeconds() * 1000 + timestamp.getNanos() / 1000000);
  return date.toLocaleString('en-US');
}
