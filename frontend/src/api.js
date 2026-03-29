export async function apiFetch(path, options) {
  const response = await fetch(path, options);
  const data = await response.json().catch(() => ({}));
  return { response, data };
}

export function getStoredUser() {
  try {
    return JSON.parse(localStorage.getItem("pe_user") || "{}");
  } catch {
    return {};
  }
}

export function setStoredUser(user) {
  localStorage.setItem("pe_user", JSON.stringify(user));
}
