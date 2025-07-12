import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Plus, Users, Search, Filter, Calendar } from 'lucide-react';
import { useAuth } from '../utils/useAuth';
import { groupsAPI } from '../services/api';
import type { Group } from '../types';

const Groups: React.FC = () => {
  const { user } = useAuth();
  const [groups, setGroups] = useState<Group[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterBy, setFilterBy] = useState<'all' | 'admin' | 'member'>('all');

  useEffect(() => {
    const loadGroups = async () => {
      try {
        const groupsData = await groupsAPI.getGroups();
        setGroups(groupsData);
      } catch (error) {
        console.error('Gruplar yüklenirken hata:', error);
      } finally {
        setLoading(false);
      }
    };

    loadGroups();
  }, []);

  // Fixed: filteredGroups logic now applies both search term and filterBy state
  const filteredGroups = groups.filter(group => {
    // Apply search filter
    const matchesSearch = searchTerm === '' || 
      group.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (group.description && group.description.toLowerCase().includes(searchTerm.toLowerCase()));
    
    // Apply role filter
    let matchesFilter = true;
    if (filterBy === 'admin') {
      matchesFilter = group.isUserAdmin === true;
    } else if (filterBy === 'member') {
      matchesFilter = group.isUserAdmin === false;
    }
    // filterBy === 'all' means no filter, so matchesFilter stays true
    
    return matchesSearch && matchesFilter;
  });

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('tr-TR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  const getFilteredGroupsCount = () => {
    return filteredGroups.length;
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <div className="flex justify-between items-center">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Gruplarım</h1>
            <p className="text-gray-600 mt-1">
              {getFilteredGroupsCount()} grup gösteriliyor
            </p>
          </div>
          <Link
            to="/groups/new"
            className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 transition-colors duration-200 flex items-center"
          >
            <Plus className="h-5 w-5 mr-2" />
            Yeni Grup
          </Link>
        </div>
      </div>

      {/* Filters */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <div className="flex flex-col md:flex-row gap-4">
          {/* Search */}
          <div className="relative flex-1">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
            <input
              type="text"
              placeholder="Grup ara..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
            />
          </div>

          {/* Filter Dropdown */}
          <div className="relative">
            <Filter className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
            <select
              value={filterBy}
              onChange={(e) => setFilterBy(e.target.value as 'all' | 'admin' | 'member')}
              className="pl-10 pr-8 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500 appearance-none bg-white"
            >
              <option value="all">Tüm Gruplar</option>
              <option value="admin">Yönetici Olduğum</option>
              <option value="member">Üye Olduğum</option>
            </select>
          </div>
        </div>
      </div>

      {/* Groups List */}
      <div className="bg-white rounded-lg shadow-md">
        {filteredGroups.length > 0 ? (
          <div className="divide-y divide-gray-200">
            {filteredGroups.map((group) => (
              <Link
                key={group.id}
                to={`/groups/${group.id}`}
                className="block p-6 hover:bg-gray-50 transition-colors duration-200"
              >
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-4">
                    <div className="p-3 bg-primary-100 rounded-lg">
                      <Users className="h-6 w-6 text-primary-600" />
                    </div>
                    <div>
                      <h3 className="text-lg font-semibold text-gray-900">
                        {group.name}
                      </h3>
                      <p className="text-gray-600 text-sm mt-1">
                        {group.description || 'Açıklama yok'}
                      </p>
                      <div className="flex items-center space-x-4 mt-2 text-sm text-gray-500">
                        <span className="flex items-center">
                          <Users className="h-4 w-4 mr-1" />
                          {group.members?.length || 0} üye
                        </span>
                        <span className="flex items-center">
                          <Calendar className="h-4 w-4 mr-1" />
                          {formatDate(group.createdAt)}
                        </span>
                      </div>
                    </div>
                  </div>
                  <div className="flex items-center space-x-2">
                    {group.isUserAdmin && (
                      <span className="bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded-full">
                        Yönetici
                      </span>
                    )}
                    <span className="bg-gray-100 text-gray-800 text-xs px-2 py-1 rounded-full">
                      {group.createdBy.fullName}
                    </span>
                  </div>
                </div>
              </Link>
            ))}
          </div>
        ) : (
          <div className="text-center py-12">
            <Users className="h-16 w-16 mx-auto mb-4 text-gray-300" />
            <h3 className="text-lg font-medium text-gray-900 mb-2">
              {searchTerm || filterBy !== 'all' ? 'Grup bulunamadı' : 'Henüz grup yok'}
            </h3>
            <p className="text-gray-600 mb-4">
              {searchTerm || filterBy !== 'all' 
                ? 'Arama kriterlerinize uygun grup bulunamadı.' 
                : 'İlk grubunuzu oluşturarak başlayın.'}
            </p>
            {(!searchTerm && filterBy === 'all') && (
              <Link
                to="/groups/new"
                className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 transition-colors duration-200 inline-flex items-center"
              >
                <Plus className="h-5 w-5 mr-2" />
                Yeni Grup Oluştur
              </Link>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default Groups;