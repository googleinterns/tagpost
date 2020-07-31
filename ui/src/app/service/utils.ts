/**
 * Common methods to be used cross all components
 */

/**
 * Convert proto timestamp to readable string
 */
export function convertTimestamp(timestamp: any): string {
  const date = new Date(timestamp.getSeconds() * 1000 + timestamp.getNanos() / 1000);
  return date.toLocaleString('en-US', { timeZone: 'UTC' });
}
