export interface AuthResponse {
    accessToken: string;
    refreshToken: string;
    roles: string[];
    user: {
      id: string;
      email: string;
      firstName: string;
      lastName: string;
      role: string;
    };
  }