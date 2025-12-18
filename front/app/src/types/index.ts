export interface User {
  id: number;
  username: string;
  email: string;
  role: 'STUDENT' | 'TEACHER' | 'ADMIN';
}

export interface AuthResponse {
  tokenType: string;
  accessToken: string;
}

export interface Category {
  id: number;
  name: string;
  description?: string;
}

export interface Outcome {
  id: number;
  description: string;
  isWinner: boolean | null;
}

export interface Event {
  id: number;
  title: string;
  description?: string;
  createdAt: string;
  updatedAt: string;
  startsAt?: string;
  endsAt?: string;
  status: 'PLANNED' | 'ONGOING' | 'COMPLETED' | 'CANCELLED';
  category: Category;
  outcomes: Outcome[];
}

export interface BetRequest {
  outcomeId: number;
  sum: number;
}

// Новая структура Bet согласно PlacedBetDto с бекенда
export interface Bet {
  id: number;
  userId: number;
  eventId: number;
  amount: number;
  placementTime: string;
  status: 'ACTIVE' | 'SETTLED';
  outcome: {
    id: number;
    description: string;
    isWinner: boolean | null;
  };
  event: {
    id: number;
    title: string;
    description?: string;
    createdAt: string;
    updatedAt: string;
    startsAt?: string | null;
    endsAt?: string | null;
    status: 'PLANNED' | 'ONGOING' | 'COMPLETED' | 'CANCELLED';
    category: {
      id: number;
      name: string;
      description?: string | null;
    };
  };
}

export interface UserRegistration {
  username: string;
  email: string;
  password: string;
}

export interface UserLogin {
  loginOrEmail: string;
  password: string;
}

export interface Wallet {
  id: number;
  balance: number;
  currency: string;
}

export interface CreateCategoryRequest {
  name: string;
  description?: string;
}

export interface CreateEventRequest {
  title: string;
  description: string;
  startsAt: string;
  endsAt: string;
  category: string;
  createdOutcomes: { description: string }[];
}

export interface EventFinishRequest {
  eventId: number;
  status: 'COMPLETED' | 'CANCELLED';
  outcomeId?: number;
}
