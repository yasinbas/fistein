import React from 'react';
import type { ReactNode } from 'react';
import { useAuth } from '../utils/useAuth';
import { LogOut, User, Home, Users, Receipt } from 'lucide-react';
import { Link, useLocation } from 'react-router-dom';

interface LayoutProps {
  children: ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  const { user, logout } = useAuth();
  const location = useLocation();

  const isActivePath = (path: string) => {
    return location.pathname === path;
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center py-4">
            {/* Logo */}
            <Link to="/" className="flex items-center">
              <Receipt className="h-8 w-8 text-primary-600 mr-2" />
              <span className="text-2xl font-bold text-gray-900">Fiştein</span>
            </Link>

            {/* Navigation */}
            <nav className="hidden md:flex items-center space-x-8">
              <Link
                to="/"
                className={`flex items-center space-x-1 px-3 py-2 rounded-lg text-sm font-medium transition-colors ${
                  isActivePath('/') 
                    ? 'bg-primary-100 text-primary-700' 
                    : 'text-gray-600 hover:text-gray-900 hover:bg-gray-100'
                }`}
              >
                <Home className="h-4 w-4" />
                <span>Ana Sayfa</span>
              </Link>
              
              <Link
                to="/groups"
                className={`flex items-center space-x-1 px-3 py-2 rounded-lg text-sm font-medium transition-colors ${
                  isActivePath('/groups') 
                    ? 'bg-primary-100 text-primary-700' 
                    : 'text-gray-600 hover:text-gray-900 hover:bg-gray-100'
                }`}
              >
                <Users className="h-4 w-4" />
                <span>Gruplar</span>
              </Link>
            </nav>

            {/* User Menu */}
            <div className="flex items-center space-x-4">
              <div className="flex items-center space-x-2">
                <User className="h-5 w-5 text-gray-600" />
                <span className="text-sm text-gray-700">{user?.fullName}</span>
              </div>
              
              <button
                onClick={logout}
                className="flex items-center space-x-1 px-3 py-2 text-sm text-gray-600 hover:text-gray-900 hover:bg-gray-100 rounded-lg transition-colors"
              >
                <LogOut className="h-4 w-4" />
                <span>Çıkış</span>
              </button>
            </div>
          </div>
        </div>
      </header>

      {/* Mobile Navigation */}
      <nav className="md:hidden bg-white border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-around py-3">
            <Link
              to="/"
              className={`flex flex-col items-center space-y-1 px-3 py-2 rounded-lg text-xs font-medium transition-colors ${
                isActivePath('/') 
                  ? 'text-primary-700' 
                  : 'text-gray-600'
              }`}
            >
              <Home className="h-5 w-5" />
              <span>Ana Sayfa</span>
            </Link>
            
            <Link
              to="/groups"
              className={`flex flex-col items-center space-y-1 px-3 py-2 rounded-lg text-xs font-medium transition-colors ${
                isActivePath('/groups') 
                  ? 'text-primary-700' 
                  : 'text-gray-600'
              }`}
            >
              <Users className="h-5 w-5" />
              <span>Gruplar</span>
            </Link>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {children}
      </main>
    </div>
  );
};

export default Layout;