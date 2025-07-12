export interface User {
  id: number;
  username: string;
  email: string;
  fullName: string;
  createdAt: string;
}

export interface Group {
  id: number;
  name: string;
  description?: string;
  createdBy: User;
  createdAt: string;
  members: GroupMember[];
  isUserAdmin?: boolean;
}

export interface GroupMember {
  id: number;
  user: User;
  joinedAt: string;
  isAdmin?: boolean;
}

export interface Expense {
  id: number;
  title: string;
  description?: string;
  amount: number;
  currency: string;
  group: Group;
  paidBy: User;
  createdAt: string;
  expenseShares: ExpenseShare[];
}

export interface ExpenseShare {
  id: number;
  user: User;
  amount: number;
  settled: boolean;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface GoogleLoginRequest {
  idToken: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  fullName: string;
  password: string;
}

export interface CreateGroupRequest {
  name: string;
  description?: string;
}

export interface CreateExpenseRequest {
  title: string;
  description?: string;
  amount: number;
  currency: string;
  groupId: number;
  memberAmounts: { [userId: number]: number };
}

export interface Balance {
  totalBalance: number;
  balanceDetails: BalanceDetail[];
}

export interface BalanceDetail {
  user: User;
  amount: number;
}

export interface GroupBalance {
  groupId: number;
  groupName: string;
  balances: BalanceDetail[];
}

// Google API types
export interface GoogleCredentialResponse {
  credential: string;
}

export interface GoogleError {
  response?: {
    data?: {
      message?: string;
    };
  };
}

export interface ApiError {
  response?: {
    data?: {
      message?: string;
    };
  };
}