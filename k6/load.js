// Generator ruchu dla order-service.
// ~10 żądań/s: 70% POST /orders, 30% GET /orders.
// INCIDENT=true dokłada kod promocyjny z "nowej kampanii marketingowej" (SUMMER25),
// którego serwis nie zna - to wyzwala incydent B.
import http from 'k6/http';
import { sleep } from 'k6';

const BASE = __ENV.BASE_URL || 'http://localhost:8080';
const INCIDENT = (__ENV.INCIDENT || 'false') === 'true';

export const options = {
  scenarios: {
    steady: {
      executor: 'constant-arrival-rate',
      rate: 10,
      timeUnit: '1s',
      duration: __ENV.DURATION || '30m',
      preAllocatedVUs: 20,
      maxVUs: 100,
    },
  },
};

function pick(arr) {
  return arr[Math.floor(Math.random() * arr.length)];
}

function promo() {
  const r = Math.random();
  if (INCIDENT && r < 0.08) return 'SUMMER25';
  if (r < 0.2) return 'WELCOME10';
  if (r < 0.3) return 'BLACKWEEK';
  return null;
}

export default function () {
  if (Math.random() < 0.7) {
    const body = {
      customerId: 1 + Math.floor(Math.random() * 20),
      sku: `SKU-${1 + Math.floor(Math.random() * 10)}`,
      quantity: 1 + Math.floor(Math.random() * 5),
      promoCode: promo(),
    };
    http.post(`${BASE}/orders`, JSON.stringify(body), {
      headers: { 'Content-Type': 'application/json' },
      tags: { name: 'POST /orders' },
    });
  } else {
    http.get(`${BASE}/orders`, { tags: { name: 'GET /orders' } });
  }
  sleep(0.1);
}
