import http from 'k6/http';
import { check } from 'k6';

export let options = {
  vus: 10,
  iterations: 10,
};

export default function () {
  const url = 'http://localhost:8080/api/coupons/4/get';
  const userId = __VU;
  const payload = JSON.stringify(userId);
  const params = { headers: { 'Content-Type': 'application/json' } };

  let res = http.post(url, payload, params);

  console.log(`VU=${__VU}, status=${res.status}, body=${res.body}`);

  // check status
  check(res, {
    'status is 200 or 400': (r) => r.status === 200 || r.status === 400,
  });
}
