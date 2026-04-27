#!/bin/sh
set -e

API_BASE_URL="${API_BASE_URL:-http://localhost:5001}"

cat > /usr/share/nginx/html/env.js << EOF
(function(window) {
  window.__env = window.__env || {};
  window.__env.apiBaseUrl = '${API_BASE_URL}';
})(this);
EOF

exec nginx -g "daemon off;"
